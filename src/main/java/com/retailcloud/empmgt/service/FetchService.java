package com.retailcloud.empmgt.service;

import com.retailcloud.empmgt.advice.exception.*;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.Projection.PrincipalRoleAndBranch;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.Projection.EmployeeInfo;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.repository.BranchRepo;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FetchService {


    private final EmployeeRepo employeeRepo;
    private final BranchRepo branchRepo;
    private final DepartmentRepo departmentRepo;
    private final CompanyRoles companyRoles;

    public Employee findByEmployeeIdAndRoleElseThrow(final Long id, final Role role) {
        return this.employeeRepo.findByEmployeeIdAndRole(id, role).orElseThrow(() -> new EmployeeNotFoundException(role));
    }

    public Branch findBranchByIdElseThrow(final Long branchId) {
        return this.branchRepo.findById(branchId).orElseThrow(BranchNotFoundException::new);
    }

    public Employee findEmployeeByIdElseThrow(Long employeeId, @NotNull final String message) {
        return this.employeeRepo.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(message));
    }


    public Department findDepartmentByIdElseThrow(Long id, @NotNull final String message) {
        return this.departmentRepo.findById(id).orElseThrow(() -> new DepartmentNotFoundException(message));
    }

    public Employee findEmployeeByIdAndRoleAndDepartmentElseThrow(final Long id, final Role role, final Department department, final String message) {
        return this.employeeRepo.findByEmployeeIdAndRoleAndDepartment(id, role, department).orElseThrow(() -> new EmployeeNotFoundException(message));
    }

    public PrincipalRoleAndBranch findRoleAndBranchIdElseThrow(final Long principalId, final String message){
        return this.employeeRepo.findRoleAndBranchIdById(principalId).orElseThrow(() -> new EmployeeAdditionFailureException(message));
    }

    public Department findDepartmentByRoleAndIdElseNull(Role role, Long departmentId) {
        Set<Role> noDepartmentRoles = this.companyRoles.noDepartmentRequired();
        if(noDepartmentRoles.contains(role))
        {
            return null;
        }
        else if (departmentId == null && !noDepartmentRoles.contains(role))
        {
            throw new DepartmentNotFoundException("Every employee below the Branch manager should be assigned to a department.");
        }
        else
        {
            /* Ignore warning for intellij, dept id won't be null by the time it reaches this block */
            return this.departmentRepo.findById(departmentId).orElse(null);
        }
    }

    public Employee findByBranchAndRoleElseNull(Branch branch, Role role) {
        return this.employeeRepo.findByBranchAndRole(branch, role).orElse(null);
    }
}
