package com.retailcloud.empmgt.model.Projection;

import com.retailcloud.empmgt.model.Projection.DepartmentInfo;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;

/**
 * Projection for {@link Employee}
 */
public interface EmployeeInfo {
    Role getRole();

    DepartmentInfo getDepartment();
}