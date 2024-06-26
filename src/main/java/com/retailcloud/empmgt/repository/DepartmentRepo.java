package com.retailcloud.empmgt.repository;

import com.retailcloud.empmgt.model.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Long>, PagingAndSortingRepository<Department, Long> {


    @Query("SELECT COUNT(d) > 0 from Department d where d.branch.branchId = :branchId and d.deptName = :deptName")
    boolean existsByBranchAndDeptName(Long branchId, String deptName);


    Optional<Department> findByDeptIdAndDeleted(final Long deptId, final  boolean fetchDeleted);

    Page<Department> findAllByDeleted(boolean deleted, Pageable pageable);
}
