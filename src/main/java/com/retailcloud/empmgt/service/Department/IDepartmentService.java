package com.retailcloud.empmgt.service.Department;

import com.retailcloud.empmgt.advice.exception.DepartmentAdditionFailureException;
import com.retailcloud.empmgt.advice.exception.EntityOperationFailureException;
import com.retailcloud.empmgt.advice.exception.OutOfScopeException;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.DepartmentDto;
import com.retailcloud.empmgt.model.payload.EmployeeDepartmentUpdate;
import com.retailcloud.empmgt.model.payload.NewDepartment;
import com.retailcloud.empmgt.model.payload.PagedEntity;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import com.retailcloud.empmgt.service.AuthorizationService;
import com.retailcloud.empmgt.service.FetchService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;




/**
 * Adding or updating department information should only be allowed for
 * Roles with chief/manager access, endpoint access should be restricted
 * from gateway/auth server using claims for better performance.
 * */
@RequiredArgsConstructor
@Service
class IDepartmentService implements DepartmentService {

    private final CompanyRoles companyRoles;
    private final DepartmentRepo departmentRepo;
    private final FetchService fetchService;
    private final EmployeeRepo employeeRepo;
    private final AuthorizationService authorizationService;



    /** Roles that should be allowed to access this endpoint -> All roles in any access/Branch Manager **/
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Department addDepartment(final NewDepartment newDepartment, final Long principalId) {


        if (this.departmentRepo.existsByBranchAndDeptName(newDepartment.branchId(), newDepartment.deptName())) {
            throw new DepartmentAdditionFailureException("The department already exists in this branch. If the department is inactive or was previously deleted, reactivate it by assigning a department head.");
        }

        /* *
        * Fetching principal can be skipped if the claims appended from the gateway
        * has more information for the authenticated user. ie branchId and Role
        *
        * Branch Managers are only allowed to add/update departments from their own branch.
        * */                                                                                /* Unknown error as principal user should be present in db as he is authenticated */
        final Employee principal = this.fetchService.findEmployeeByIdElseThrow(principalId, "Unknown error, failed to proceed!");
        Branch principalBranch = null;

        /*
         * Not part of any access authority meaning principal could be a part of
         * a branch ie manager/similar roles that will be added in the future.
         * */
        if (!companyRoles.anyAccessAuthority().contains(principal.getRole())) {
            principalBranch = principal.getBranch();
            if(!Objects.equals(principalBranch.getBranchId(), newDepartment.branchId())) {
                throw new OutOfScopeException("User doesn't have the necessary permissions to create a department in a branch other than their own branch.");
            }
        }

        final Branch branch = principalBranch != null ? principalBranch :
                              this.fetchService.findBranchByIdElseThrow(newDepartment.branchId());

        final Employee deptHeadToBe = newDepartment.deptHeadId() == null ? null :
                this.fetchService.findEmployeeByIdElseThrow(newDepartment.deptHeadId(), "Employee not found!");


        if (deptHeadToBe == null && newDepartment.activateDepartment()) {
            throw new DepartmentAdditionFailureException("Department cannot be activated with out a department head, recreate again with a department head!");
        }

        Department department = Department.builder()
                .deptName(newDepartment.deptName())
                .deptHead(deptHeadToBe)
                .branch(branch)
                .isActive(newDepartment.activateDepartment())
                .creationDate(LocalDate.now())
                .build();

        if(deptHeadToBe != null){
            if(deptHeadToBe.getRole() == Role.BRANCH_DEPARTMENT_HEAD){
                throw new EntityOperationFailureException("The assigned department head is already allocated to a department as it's head!");
            }
            return this.assignNewHeadForDept(department, deptHeadToBe, false, null, false);
        }

        return this.departmentRepo.save(department);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Department assignNewHeadForDept(EmployeeDepartmentUpdate update, Long principalId)
    {
        Department department = this.fetchService.findDepartmentByIdElseThrow(update.deptId(), "Failed to find department!");

        this.authorizationService.validateUserPermissionsForDepartment(principalId, department);

        Employee employee = this.fetchService.findEmployeeByIdElseThrow(update.movableEmployeeId(), "Operation failed: failure finding employee.");

        this.employeeRepo.updateRmForLowLevelEmployees(employee, null);

        employee.setDepartment(department);
        employee.setBranch(department.getBranch());
        employee.setRole(Role.BRANCH_DEPARTMENT_HEAD);

        this.updateDepartmentAndPrevHead(department);

        department.setDeptHead(employee);
        /* Update reporting manager for new dept head */
        Employee reportingManager = this.fetchService.findByBranchAndRoleElseNull(department.getBranch(), Role.BRANCH_MANAGER, null);
        employee.setReportingManager(reportingManager);
        department = departmentRepo.save(department);

        /* After updating the dept head assign the reporting manager for the team lead as the new employee */
        this.updateTeamLeadsAfterNewDeptHead(department);

        return department;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Department assignNewHeadForDept(final Department department,
                                           final Employee employee,
                                           final boolean validatePermissions,
                                           final Long principalId,
                                           final boolean updateRmForTeamsLeads) {


        if(employee.getRole()  == Role.BRANCH_DEPARTMENT_HEAD){
            employee.getDepartment().setDeptHead(null);
        }

        if(validatePermissions){
            this.authorizationService.validateUserPermissionsForDepartment(principalId, department);
        }

        if(Role.JUNIOR_ASSISTANT.getRolesAbove().contains(employee.getRole()) && employee.getEmployeeId() != null){
            this.employeeRepo.updateRmForLowLevelEmployees(employee, null);
        }

        this.updateDepartmentAndPrevHead(department);
        employee.setDepartment(department);
        employee.setBranch(department.getBranch());
        /* After updating the dept head assign the reporting manager for the team lead as the new employee */
        if(updateRmForTeamsLeads) this.updateTeamLeadsAfterNewDeptHead(department);

        department.setDeptHead(employee);
        return this.departmentRepo.save(department);
    }

    private void updateTeamLeadsAfterNewDeptHead(Department department) {
        /* After updating the dept head assign the reporting manager for the team lead as the new employee */
        this.employeeRepo.updateRmForTeamLeads(department, department.getDeptHead(), Role.TEAM_LEAD);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    void updateDepartmentAndPrevHead(Department department) {
        Employee prevDeptHead = department.getDeptHead();
        if(prevDeptHead != null){
            prevDeptHead.setRole(Role.UNDEFINED);
            prevDeptHead.setDepartment(null);
            prevDeptHead.setReportingManager(null);
            this.employeeRepo.save(prevDeptHead);
        }
        else
        {
            department.setIsActive(true);
            department.setDeleted(false);
        }

    }


    /**
     * Only authorized user's should be able to access this endpoint.
     * ie BranchManagers, COO, Initial acc
     * */
    @Override
    public void deleteDepartment(Long deptId, Long principalId) {

        Department department = this.fetchService.findDepartmentByIdElseThrow(deptId, "Failed to delete department! Reason: Department not found.");

        /* Check if the user has any access roles, if so proceed. Else check
           if the user is a manager and is part of the same branch. */
        this.authorizationService.validateUserPermissionsForDepartment(principalId, department);

        final Long employeeCount = this.employeeRepo.countByDepartmentAndExitDate(department, null);

        if(employeeCount > 0){
            throw new EntityOperationFailureException("Cannot delete department as there are employees assigned to this department!");
        }

        department.setDeleted(true);
        department.setIsActive(false);

        this.departmentRepo.save(department);
    }

    @Override
    public PagedEntity<DepartmentDto> fetchDepartments(Integer page, Integer count)
    {
        if(page == null || page < 1) page = 1;
        if(count == null || count < 1) count = 20;
        else if(count > 30) count = 30;

        PageRequest pageRequest = PageRequest.of(page - 1, count);
        Page<Department> departmentPage = this.departmentRepo.findAllByDeleted(false, pageRequest);

        List<DepartmentDto> departmentDtos = departmentPage.getContent().stream().map(ModelMapper::toDto).toList();
        return PagedEntity.<DepartmentDto>builder()
                .entityList(departmentDtos)
                .page(page)
                .hasNext(departmentPage.hasNext())
                .hasPrev(departmentPage.hasPrevious())
                .totalPages(departmentPage.getTotalPages())
                .build();
    }


}
