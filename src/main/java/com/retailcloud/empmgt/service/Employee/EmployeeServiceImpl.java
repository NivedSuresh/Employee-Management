package com.retailcloud.empmgt.service.Employee;

import com.retailcloud.empmgt.advice.exception.EntityOperationFailureException;
import com.retailcloud.empmgt.advice.exception.OutOfScopeException;
import com.retailcloud.empmgt.advice.exception.RoleCannotBeAssignedException;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.Projection.RoleAndBranchId;
import com.retailcloud.empmgt.model.entity.Address;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.NewEmployee;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import com.retailcloud.empmgt.service.FetchService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;


@RequiredArgsConstructor
@Service
class EmployeeServiceImpl implements EmployeeService
{


    private final EmployeeRepo employeeRepo;
    private final FetchService fetchService;
    private final CompanyRoles companyRoles;


    public Employee addEmployee(final NewEmployee newEmployee, final Long principalId)
    {

        /*
         * Fetch can be avoided if the role and branchId is appended to the req after authorization
         * */
        final RoleAndBranchId roleAndBranchId = this.fetchService.findRoleAndBranchIdElseThrow(principalId, "An unknown error occurred, if this was from our side please let us know!");

        /*
         * Important check: check to see if principal is assigning the user an authority >= his scope
         * */
        Set<Role> rolesAbove = newEmployee.role().getRolesAbove();
        if(rolesAbove != null && rolesAbove.contains(newEmployee.role())){
            throw new RoleCannotBeAssignedException();
        }

        /*
        * Check if principal is assigning an employee to a different branch than his own without necessary permissions.
        * */
        if(!companyRoles.anyAccessAuthority().contains(roleAndBranchId.role()) && !Objects.equals(newEmployee.branchId(), roleAndBranchId.branchId())){
            throw new OutOfScopeException("User doesn't have the necessary permissions to add an employee to a branch other than their own branch.");
        }

        final Department department = newEmployee.departmentId() != null ? fetchService.findDepartmentByIdElseThrow(newEmployee.departmentId(), "Operation failed: Failed to find department"): null;

        if(department != null && !Objects.equals(department.getBranch().getBranchId(), newEmployee.branchId()))
        {
            throw new EntityOperationFailureException("The department should belong to the same branch as the employee!");
        }

        final Role reportingManagerRoleTobe = newEmployee.role().getReportingManagerRole();

        final Employee reportingManager = newEmployee.reportingManagerId() == null ? null :
                this.fetchService.findEmployeeByIdAndRoleAndDepartmentElseThrow(newEmployee.reportingManagerId(), reportingManagerRoleTobe, department,  "Operation failed as reporting manager cannot be assigned!");

        final Address address = ModelMapper.toEntity(newEmployee.personalAddress(), false);

        final Employee employee = Employee.builder()
                .firstName(newEmployee.firstName())
                .middleName(newEmployee.middleName())
                .lastName(newEmployee.lastName())
                .dob(newEmployee.dob())
                .employeeJoinDate(newEmployee.employeeJoinDate())
                .department(department)
                .role(newEmployee.role())
                .yearlyBonusPercentage(newEmployee.yearlyBonusPercentage())
                .reportingManager(reportingManager)
                .personalAddress(address)
                .build();

        return this.employeeRepo.save(employee);
    }
}
