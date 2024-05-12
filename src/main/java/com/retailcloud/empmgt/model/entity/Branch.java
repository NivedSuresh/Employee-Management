package com.retailcloud.empmgt.model.entity;

import jakarta.persistence.*;
import lombok.*;



/**
 * Represents a specific branch of the company located at a particular location.
 * ex: {RetailCloud - Calicut, Kerala} , {RetailCloud - Concord, California}
 */


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    @Column(nullable = false)
    private String buildingName;

    private String street;

    private String city;

    private String state;

    @Column(unique = true, nullable = false)
    private String zipcode;

    private String country;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Version
    private long version;
}
