package com.retailcloud.empmgt.model.entity;

import com.retailcloud.empmgt.model.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Employee {
    @Id
    @Column(name = "employee_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;


    @Column(name = "first_name", nullable = false)
    private String firstName;


    @Column(name = "middle_name")
    private String middleName;


    @Column(name = "last_name", nullable = false)
    private String lastName;


    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob;


    private LocalDate employeeJoinDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;


    @Enumerated(EnumType.STRING)
    private Role role;


    private Double yearlyBonusPercentage;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "personal_address_id")
    private Address personalAddress;


    /* Branch can be set to lazy as branch will be fetched by department
     * which is fetched eagerly which fetches branch eagerly */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_id")
    private Branch branchDetails;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employee")
    private List<MonthlySalaryMeta> paidSalaries;


    private LocalDateTime exitDate;


    @Version
    private long version;
}
