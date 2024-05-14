package com.retailcloud.empmgt.model.payload;

import jakarta.validation.constraints.NotNull;

public record EmployeeDepartmentUpdate(

        @NotNull(message = "Please select a valid department!")
        Long deptId,

        @NotNull(message = "Please select a valid employee!")
        Long movableEmployeeId
) {
}
