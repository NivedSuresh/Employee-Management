package com.retailcloud.empmgt.model.payload;

import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class DepartmentDto {
    private Long deptId;
    private String deptName;
    private Long deptHeadId;
    private String deptHeadFullName;
    private LocalDate creationDate;
    private Boolean isActive;
    private BranchDto branch;
    private Integer employeeCount;

}
