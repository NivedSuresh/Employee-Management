package com.retailcloud.empmgt.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MonthlySalaryMeta {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Employee employee;

    private LocalDateTime transactionTimestamp;

    private Double salaryAmount;

    private Double bonusAmount;

    private Double totalAmount;

    @Column(name = "year_month")
    private YearMonth yearMonth;

    private Integer totalHoursWorked;

    @Version
    private Long version;

}
