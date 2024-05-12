package com.retailcloud.empmgt.service;

import com.retailcloud.empmgt.advice.exception.BranchNotFoundException;
import com.retailcloud.empmgt.advice.exception.DepartmentNotFoundException;
import com.retailcloud.empmgt.advice.exception.EmployeeNotFoundException;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.repository.BranchRepo;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FetchService {


    private final EmployeeRepo employeeRepo;
    private final BranchRepo branchRepo;
    private final DepartmentRepo departmentRepo;

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
}
