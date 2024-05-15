package com.retailcloud.empmgt.model.payload;

public record DepartmentMeta(
        DepartmentDto departmentDto,
        PagedEntity<EmployeeDto> employees
) {
}
