package com.retailcloud.empmgt.service.Employee;

import com.retailcloud.empmgt.advice.exception.*;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.Projection.PrincipalRoleAndBranch;
import com.retailcloud.empmgt.model.entity.*;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.EmployeeDepartmentUpdate;
import com.retailcloud.empmgt.model.payload.NewEmployee;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import com.retailcloud.empmgt.service.AuthorizationService;
import com.retailcloud.empmgt.service.Department.DepartmentService;
import com.retailcloud.empmgt.service.FetchService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;


@RequiredArgsConstructor
@Service
class EmployeeServiceImpl implements EmployeeService
{


    private final EmployeeRepo employeeRepo;
    private final FetchService fetchService;
    private final CompanyRoles companyRoles;
    private final DepartmentService departmentService;
    private final AuthorizationService authorizationService;


    @Transactional(propagation = Propagation.REQUIRED)
    public Employee addEmployee(final NewEmployee newEmployee, final Long principalId)
    {

        this.validateRequest(newEmployee, principalId);

        final Department department = this.fetchService.findDepartmentByRoleAndIdElseNull(newEmployee.role(), newEmployee.departmentId());
        if(newEmployee.departmentId() != null && department == null){
            throw new DepartmentNotFoundException("Operation failed: Failed to associate a department with the employee. NB: Do not try to assign a department to authorities above Dept Head.");
        }


        Employee employee = Employee.builder().build();

        if (department != null)
        {
            if(!department.getIsActive() && newEmployee.role() != Role.BRANCH_DEPARTMENT_HEAD){
                throw new DepartmentNotActiveException();
            }
            // Ensuring that the department belongs to the same branch as the employee.
            if (!Objects.equals(department.getBranch().getBranchId(), newEmployee.branchId())) {
                throw new EntityOperationFailureException("The department should belong to the same branch as the employee!");
            }
        }
        if(newEmployee.role() == Role.BRANCH_DEPARTMENT_HEAD && department == null){
            throw new DepartmentNotFoundException("Department not found!");
        }

        // Fetching the reporting manager if reporting manager ID is provided in the request.
        // Exception is thrown if reporting manager role doesn't align with employee role.
        final Employee reportingManager = this.fetchService.fetchReportingManager(newEmployee.reportingManagerId(), newEmployee.role(), department);

        final Branch branch = getBranchForNewEmployee(newEmployee, department, reportingManager);

        /* Untag previous branch manager before persisting the employee */
        if(employee.getRole() == Role.BRANCH_MANAGER)
        {
            this.untagPreviousManager(branch);
        }

        final Address address = ModelMapper.toEntity(newEmployee.personalAddress(), false);


        employee.setFirstName(newEmployee.firstName());
        employee.setMiddleName(newEmployee.middleName());
        employee.setLastName(newEmployee.lastName());
        employee.setDob(newEmployee.dob());
        employee.setEmployeeJoinDate(newEmployee.employeeJoinDate());
        employee.setDepartment(department);
        employee.setRole(newEmployee.role());
        employee.setYearlyBonusPercentage(newEmployee.yearlyBonusPercentage());
        employee.setSalary(newEmployee.salary());
        employee.setReportingManager(reportingManager);
        employee.setPersonalAddress(address);
        employee.setBranch(branch);

        /* If the newly added employee is a dept head update the same in department
           table as well Update reporting manager for all team leads under this dept. */
        if(newEmployee.role() == Role.BRANCH_DEPARTMENT_HEAD)
        {
            Department updatedDept = this.departmentService.assignNewHeadForDept(department, employee, false, null, true);

            return updatedDept.getDeptHead();
        }

        employee = this.employeeRepo.save(employee);

        if(employee.getRole() == Role.BRANCH_MANAGER)
        {
            /* Set new employee as the reporting manager for every dept head */
           this.employeeRepo.updateRmForDeptHeads(employee.getBranch(), employee, Role.BRANCH_DEPARTMENT_HEAD);
        }

        return employee;
    }

    private Employee untagPreviousManager(Branch branch) {
        if(branch == null) throw new BranchNotFoundException();
        Employee prevManager = this.fetchService.findByBranchAndRoleElseNull(branch, Role.BRANCH_MANAGER, null);
        if(prevManager != null){
            prevManager.setRole(Role.UNDEFINED);
            prevManager.setReportingManager(null);
            prevManager = this.employeeRepo.save(prevManager);
        }
        return prevManager;
    }

    private void validateRequest(NewEmployee newEmployee, Long principalId) {
        /* If user is not a COO and a branchId was still not provided throw exception */
        if(!this.companyRoles.noBranchRequired().contains(newEmployee.role()) &&
                newEmployee.branchId() == null)
        {
            throw new BranchNotFoundException();
        }

        // This step can potentially be optimized by appending the role and branch ID to the request after authorization from the gateway.
        final PrincipalRoleAndBranch principalMeta = this.fetchService.findRoleAndBranchIdElseThrow(principalId);


        // Important check: Ensuring that the principal is not assigning a role higher than their own scope to the new employee.
        final Set<Role> nonAssignableRoles = principalMeta.getRole().getRolesAbove();
        if (nonAssignableRoles.contains(newEmployee.role())) {
            throw new RoleCannotBeAssignedException();
        }

        try{
            // Checking if the principal is assigning the employee to a different branch without necessary permissions.
            if (!companyRoles.anyAccessAuthority().contains(principalMeta.getRole()) && !Objects.equals(newEmployee.branchId(), principalMeta.getBranch().getBranchId())) {
                throw new OutOfScopeException("User doesn't have the necessary permissions to add an employee to a branch other than their own branch.");
            }
        }
        catch (NullPointerException e){
            throw new OutOfScopeException("User doesn't have the necessary permissions to add an employee to a branch other than their own branch.");
        }
    }

    public Branch getBranchForNewEmployee(NewEmployee newEmployee, Department department, Employee reportingManager) {
        Set<Role> noBranchRequired = companyRoles.noBranchRequired();

        if(department != null)
        {
            return department.getBranch();
        }
        else if (noBranchRequired.contains(newEmployee.role()))
        {
            return null;
        }
        else if (!noBranchRequired.contains(reportingManager.getRole()))
        {
            /* Don't call get branch directly as it's lazily loaded */
            return reportingManager.getDepartment().getBranch();
        }
        return this.fetchService.findBranchByIdElseThrow(newEmployee.branchId());
    }


    /**
     * Access to the endpoint should be limited to COO & Branch Manager.
     * <br>
     * If employee is a Branch Manager or COO throw exception.
     * */
    @Override
    public Employee moveEmployeeToDepartment(EmployeeDepartmentUpdate update, Long principalId) {

        Employee employee = this.fetchService.findEmployeeByIdElseThrow(update.movableEmployeeId(), "Couldn't move employee as employee cannot be found!");
        if(employee.getDepartment() != null && Objects.equals(employee.getDepartment().getDeptId(), update.deptId())){
            throw new EntityOperationFailureException("Employee is already part of the department!");
        }

        if(Role.BRANCH_DEPARTMENT_HEAD.getRolesAbove().contains(employee.getRole())){
            throw new EmployeeCannotBeMovedException("Employee cannot be moved as the role of the current employee");
        }

        if(employee.getRole() == Role.BRANCH_DEPARTMENT_HEAD){
            Department department = this.departmentService.assignNewHeadForDept(update, principalId);
            return department.getDeptHead();
        }


        final PrincipalRoleAndBranch roleAndBranch = this.fetchService.findRoleAndBranchIdElseThrow(principalId);
        /* Check is principal has permissions to do updates on employee department */
        this.authorizationService.validateUserPermissionsForDepartment(employee.getDepartment(), roleAndBranch);

        /* Check is principal has permissions to do updates on the department which employee should be moved to */
        final Department deptTobeMovedTo = this.fetchService.findDepartmentByIdElseThrow(update.deptId(), "Failed to find the department!");
        if(!deptTobeMovedTo.getIsActive()){
            throw new DepartmentNotActiveException();
        }
        this.authorizationService.validateUserPermissionsForDepartment(deptTobeMovedTo, roleAndBranch);


        /* Check if employee is team lead of any department if so update junior/senior assistants reporting manager */
        if(employee.getRole() == Role.TEAM_LEAD){
            this.employeeRepo.updateRmForLowLevelEmployees(employee, null);
        }

        employee.setDepartment(deptTobeMovedTo);
        employee.setBranch(deptTobeMovedTo.getBranch());
        return this.employeeRepo.save(employee);
    }



}
