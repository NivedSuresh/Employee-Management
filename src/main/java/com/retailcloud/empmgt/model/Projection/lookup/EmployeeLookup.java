package com.retailcloud.empmgt.model.Projection.lookup;

import com.retailcloud.empmgt.model.entity.Employee;

/**
 * Projection for {@link Employee}
 */
public interface EmployeeLookup {
    Long getEmployeeId();

    String getFirstName();

    String getMiddleName();

    String getLastName();
}