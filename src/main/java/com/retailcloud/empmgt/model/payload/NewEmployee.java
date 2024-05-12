package com.retailcloud.empmgt.model.payload;

import com.retailcloud.empmgt.model.entity.enums.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NewEmployee(
        @NotEmpty(message = "Please provide the first name")
        String firstName,

        String middleName,

        @NotEmpty(message = "Please provide the last name")
        String lastName,

        @NotNull(message = "Please provide the date of birth")
        @Past(message = "Date of birth should be in the past")
        LocalDate dob,

        @NotNull(message = "Please provide the employee join date")
        @PastOrPresent(message = "Employee join date should be in the past or present")
        LocalDate employeeJoinDate,

        @NotNull(message = "Please provide the department ID")
        Long departmentId,

        @NotNull(message = "Please provide the employee role")
        Role role,

        @NotNull(message = "Please provide the yearly bonus percentage")
        @DecimalMin(value = "0.0", message = "Yearly bonus percentage must be greater than or equal to 0")
        @DecimalMax(value = "100.0", message = "Yearly bonus percentage must be less than or equal to 100")
        Double yearlyBonusPercentage,

        Long reportingManagerId,

        @NotNull(message = "Please provide the personal address")
        NewAddress personalAddress,

        @NotNull(message = "Please provide the branch ID")
        Long branchId,

        LocalDateTime exitDate
) {}
