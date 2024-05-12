package com.retailcloud.empmgt.service.Employee;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.EmployeeDto;
import com.retailcloud.empmgt.model.payload.NewEmployee;

public interface EmployeeService {

    Employee addEmployee(final NewEmployee newEmployee, final Long principalId);

}
