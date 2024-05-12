package com.retailcloud.empmgt.model.entity;

import com.retailcloud.empmgt.model.entity.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String buildingName;

    private String street;

    private String city;

    private String state;

    private Integer zipcode;

    private String country;

    private AddressType addressType;

    private String contactPhoneNumber;

    @Version
    private long version;

}
