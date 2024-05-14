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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FetchService {


    private final EmployeeRepo employeeRepo;
    private final BranchRepo branchRepo;
    private final DepartmentRepo departmentRepo;
    private final CompanyRoles companyRoles;

    public Employee findByEmployeeIdAndRoleElseThrow(final Long id, final Role role, final boolean fetchDeleted) {
        return this.employeeRepo.findByEmployeeIdAndRoleAndExitDate(id, role, null).orElseThrow(() -> new EmployeeNotFoundException(role));
    }

    public Branch findBranchByIdElseThrow(final Long branchId) {
        return this.branchRepo.findById(branchId).orElseThrow(BranchNotFoundException::new);
    }

    public Employee findEmployeeByIdElseThrow(Long employeeId, @NotNull final String message) {
        return this.employeeRepo.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(message));
    }


    public Department findDepartmentByIdElseThrow(Long id, @NotNull final String message) {
        return this.departmentRepo.findByDeptIdAndDeleted(id, false).orElseThrow(() -> new DepartmentNotFoundException(message));
    }

    public Employee findEmployeeByIdAndRoleAndDepartmentElseThrow(final Long id, final Role role, final Department department, LocalDateTime exitDate, final String message) {
        return this.employeeRepo.findByEmployeeIdAndRoleAndDepartmentAndExitDate(id, role, department, exitDate).orElseThrow(() -> new EmployeeNotFoundException(message));
    }

    public PrincipalRoleAndBranch findRoleAndBranchIdElseThrow(final Long principalId){
        return this.employeeRepo.findRoleAndBranchIdById(principalId).orElseThrow(() -> new EmployeeAdditionFailureException("An unknown error occurred, if this was from our side please let us know!"));
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
            return this.departmentRepo.findByDeptIdAndDeleted(departmentId, false).orElse(null);
        }
    }

    public Employee findByBranchAndRoleElseNull(Branch branch, Role role, LocalDateTime exitDate) {
        return this.employeeRepo.findByBranchAndRoleAndExitDate(branch, role, exitDate).orElse(null);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public Employee fetchReportingManager(final Long reportingManagerId, final Role newEmployeeRole, final Department department)
    {
        final String exMessage = "Reporting manager not found!";
        switch (newEmployeeRole)
        {
            case JUNIOR_ASSISTANT, SENIOR_ASSISTANT ->
            {
                return this.findEmployeeByIdAndRoleAndDepartmentElseThrow(reportingManagerId, Role.JUNIOR_ASSISTANT.getReportingManagerRole(), department, null, exMessage);
            }
            case TEAM_LEAD ->
            {
                return department != null ? department.getDeptHead() : null;
            }
            case BRANCH_DEPARTMENT_HEAD, BRANCH_MANAGER ->
            {
                return this.findByEmployeeIdAndRoleElseThrow(reportingManagerId, newEmployeeRole.getReportingManagerRole(), false);
            }
            default ->
            {
                return null;
            }
        }
    }

    public List<Employee> findByRoleAndDepartmentElseEmpty(Role role, Department department, LocalDateTime exitDate) {
        return this.employeeRepo.findByRoleAndDepartmentAndExitDate(role, department, exitDate).orElse(List.of());
    }
}
