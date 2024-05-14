package com.retailcloud.empmgt.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name = "unqiue_dept_per_branch", columnNames = {"branch", "deptName"})})
@Entity
public class Department {

    @Id
    @Column(name = "dept_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column(nullable = false)
    private String deptName;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "dept_head_id")
    private Employee deptHead;

    @Column(name = "created_on", updatable = false)
    private LocalDate creationDate;

    @Column(name = "is_active")
    private Boolean isActive;

    private boolean deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", updatable = false, nullable = false)
    private Branch branch;

    @Version
    private long version;

}
