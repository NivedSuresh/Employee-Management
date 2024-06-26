package com.retailcloud.empmgt.repository;

import com.retailcloud.empmgt.model.Projection.PrincipalRoleAndBranch;
import com.retailcloud.empmgt.model.Projection.lookup.EmployeeLookup;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long>, PagingAndSortingRepository<Employee, Long> {

    Optional<Employee> findByEmployeeIdAndRoleAndExitDate(Long employeeId, Role role, LocalDateTime exitDate);

    Optional<Employee> findByEmployeeIdAndRoleAndDepartmentAndExitDate(final Long employeeId, final Role role, final Department department, final LocalDateTime exitDate);

    @Query("SELECT e from Employee as e where e.employeeId = :principalId")
    Optional<PrincipalRoleAndBranch> findRoleAndBranchIdById(Long principalId);


    Optional<Employee> findByBranchAndRoleAndExitDate(final Branch branch, final Role role, final LocalDateTime exitDate);


    @Query("SELECT count(e) FROM Employee e WHERE e.department = :department AND e.exitDate = :exitDate")
    Long findCountOfEmployeesByDeptIdAndExitDate(Department department, @Param("exitDate") LocalDateTime exitDate);


    Long countByDepartmentAndExitDate(Department department, LocalDateTime exitDate);

    @Modifying
    @Query("UPDATE Employee as e set e.reportingManager = :newHead where e.department = :department and e.role = :role")
    void updateRmForTeamLeads(Department department, Employee newHead, Role role);

    @Modifying
    @Query("UPDATE Employee as e set e.reportingManager = :newHead where e.branch = :branch and e.role = :role")
    Integer updateRmForDeptHeads(Branch branch, Employee newHead, Role role);

    Optional<List<Employee>> findActiveEmployeesByRoleAndDepartmentAndExitDate(Role role, Department department, LocalDateTime exitDate);



    @Modifying
    @Query("UPDATE Employee as e set e.reportingManager = :newRm where e.reportingManager = :prevRm")
    void updateRmForLowLevelEmployees(Employee prevRm, Employee newRm);

    Optional<List<Employee>> findActiveEmployeesByDepartmentAndExitDate(Department department, LocalDateTime exitDate);


    Page<Employee> findAllByExitDate(LocalDateTime exitDate, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.department.deptId = :deptId AND e.exitDate IS NULL")
    Page<Employee> findActiveEmployeesByDepartmentAndExitDate(Long deptId, Pageable pageable);


    @Query("select e from Employee as e where e.department.deptId = :deptId and e.role = :role and e.exitDate IS NULL")
    Page<Employee> findActiveEmployeesByRoleAndDepartmentAndExitDate(Role role, Long deptId, Pageable pageable);

    Optional<List<Employee>> findAllByReportingManager(Employee employee);

    @Query("SELECT e from Employee as e where e.exitDate is NULL")
    Page<EmployeeLookup> lookupActiveEmployees(Pageable pageRequest);
}
