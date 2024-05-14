package com.retailcloud.empmgt.service.Employee;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.EmployeeDepartmentUpdate;
import com.retailcloud.empmgt.model.payload.NewEmployee;

public interface EmployeeService {

    Employee addEmployee(final NewEmployee newEmployee, final Long principalId);

    Employee moveEmployeeToDepartment(EmployeeDepartmentUpdate update, Long principalId);
}
