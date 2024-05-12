package com.retailcloud.empmgt.model.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record NewDepartment(
        @NotBlank(message = "Department name cannot be empty!")
        @Size(min = 2, message = "Department name should consist of minimum 2 characters!")
        String deptName,
        Long deptHeadId,
        @NotNull(message = "Please provide details whether to activate the department on creation or not?")
        Boolean activateDepartment,

        @NotNull(message = "Select a branch before assigning a new department!")
        @Min(value = 1, message = "Please select a valid branch!")
        Long branchId
) {
}
