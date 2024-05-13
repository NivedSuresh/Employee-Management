package com.retailcloud.empmgt.model.payload;

import com.retailcloud.empmgt.model.entity.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class EmployeeDto {

    private Long employeeId;

    private String firstName;

    private String middleName;

    private String lastName;

    private LocalDate dob;

    private LocalDate employeeJoinDate;

    private DepartmentDto department;

    private Role role;

    private Double yearlyBonusPercentage;

    private LocalDateTime exitDate;

    private EmployeeDto reportingManager;

    private BranchDto branch;

}
