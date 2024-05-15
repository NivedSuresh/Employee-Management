package com.retailcloud.empmgt;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmployeeLookupDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private int employeeId;
}
