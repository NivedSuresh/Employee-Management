package com.retailcloud.empmgt.service.Employee;

import com.retailcloud.empmgt.model.Projection.lookup.EmployeeLookup;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.*;

import java.time.LocalDateTime;

public interface EmployeeService {

    Employee addEmployee(final NewEmployee newEmployee, final Long principalId);

    Employee moveEmployeeToDepartment(EmployeeDepartmentUpdate update, Long principalId);

    PagedEntity<? extends EmployeeLookup> fetchAllByExitDate(LocalDateTime exitDate, Integer page, Integer count, Boolean lookup);

    DepartmentMeta expandDepartment(Long deptId, String expand, Integer page);
}
