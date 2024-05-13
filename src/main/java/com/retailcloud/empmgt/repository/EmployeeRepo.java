package com.retailcloud.empmgt.repository;

import com.retailcloud.empmgt.model.Projection.PrincipalRoleAndBranch;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.Projection.EmployeeInfo;
import com.retailcloud.empmgt.model.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long>, PagingAndSortingRepository<Employee, Long> {

    Optional<Employee> findByEmployeeIdAndRole(Long id, Role role);

    Optional<Employee> findByEmployeeIdAndRoleAndDepartment(final Long employeeId, final Role role, final Department department);

    @Query("SELECT e from Employee as e where e.employeeId = :principalId")
    Optional<PrincipalRoleAndBranch> findRoleAndBranchIdById(Long principalId);

    Optional<Employee> findByBranchAndRole(final Branch branch, final Role role);

    Optional<Employee> findByFirstName(String name);
}
