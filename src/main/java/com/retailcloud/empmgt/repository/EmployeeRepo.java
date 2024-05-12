package com.retailcloud.empmgt.repository;

import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long>, PagingAndSortingRepository<Employee, Long> {

    Optional<Employee> findByEmployeeIdAndRole(Long id, Role role);

    Optional<Employee> findByEmployeeIdAndRoleAndDepartment(final Long employeeId, final Role role, final Department department);

}
