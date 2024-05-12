package com.retailcloud.empmgt.model.payload;

import lombok.Builder;



@Builder
public record BranchDto(
        Long branchId,
        String buildingName,
        String street,
        String city,
        String state,
        String zipcode,
        String country,
        String phoneNumber,
        String email
)
{}
