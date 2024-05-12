package com.retailcloud.empmgt.service.Department;

import com.retailcloud.empmgt.advice.exception.DepartmentAdditionFailureException;
import com.retailcloud.empmgt.advice.exception.NoAccessToCreateDepartmentException;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.DeptHeadUpdate;
import com.retailcloud.empmgt.model.payload.NewDepartment;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.service.FetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class IDepartmentService implements DepartmentService {

    private final CompanyRoles companyRoles;
    private final DepartmentRepo departmentRepo;
    private final FetchService fetchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Department addDepartment(final NewDepartment newDepartment, final Long principalId) {


        if (this.departmentRepo.existsByBranchAndDeptName(newDepartment.branchId(), newDepartment.deptName())) {
            throw new DepartmentAdditionFailureException("Department already exists for the branch!");
        }

        /*
        * Fetching principal can be skipped if the claims appended from the gateway
        * has more information for the authenticated user. ie branchId and Role
        * */                                                                                /* Unknown error as principal user should be present in db as he is authenticated */
        final Employee principal = this.fetchService.findEmployeeByIdElseThrow(principalId, "Unknown error, failed to proceed!");
        Branch principalBranch = null;

        /* *
         * If the authenticated user's (branch Id != new dept branch id) or role
         * doesn't have sufficient authority, throw NoAccessToCreateDepartmentException.
         * */
        if (!companyRoles.anyAccessAuthority().contains(principal.getRole())) {
            principalBranch = principal.getBranchDetails();
            if(!Objects.equals(principalBranch.getBranchId(), newDepartment.branchId())) {
                throw new NoAccessToCreateDepartmentException();
            }
        }

        final Branch branch = principalBranch != null ? principalBranch :
                              this.fetchService.findBranchByIdElseThrow(newDepartment.branchId());

        final Employee deptHead = newDepartment.deptHeadId() == null ? null :
                this.fetchService.findByEmployeeIdAndRoleElseThrow(
                        newDepartment.branchId(), Role.BRANCH_DEPARTMENT_HEAD
                );

        if (deptHead == null && newDepartment.activateDepartment()) {
            throw new DepartmentAdditionFailureException("Department cannot be activated with out a department head, recreate again with a department head!");
        }

        Department department = Department.builder()
                .deptName(newDepartment.deptName())
                .deptHead(deptHead)
                .employeeCount(deptHead == null ? 0 : 1)
                .branch(branch)
                .isActive(newDepartment.activateDepartment())
                .creationDate(LocalDate.now())
                .build();

        return this.departmentRepo.save(department);
    }

    @Transactional
    @Override
    public Department assignNewHeadForDept(DeptHeadUpdate update)
    {
        Department department = this.fetchService.findDepartmentByIdElseThrow(update.deptId(), "Failed to find department!");
        Employee employee = this.fetchService.findEmployeeByIdElseThrow(update.newHeadId(), "Operation failed: failure finding employee.");

        employee.setDepartment(department);
        employee.setRole(Role.BRANCH_DEPARTMENT_HEAD);

        Employee prevDeptHead = department.getDeptHead();
        prevDeptHead.setRole(Role.UNDEFINED);

        department.setDeptHead(employee);
        return departmentRepo.save(department);
    }


}
