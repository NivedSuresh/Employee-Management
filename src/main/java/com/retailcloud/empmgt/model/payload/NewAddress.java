package com.retailcloud.empmgt.model.payload;

import com.retailcloud.empmgt.model.entity.enums.AddressType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewAddress(

        Long addressId,

        @NotEmpty(message = "Building name must not be empty")
        String buildingName,

        @NotEmpty(message = "Street must not be empty")
        String street,

        @NotEmpty(message = "City must not be empty")
        String city,

        @NotEmpty(message = "State must not be empty")
        String state,

        @NotEmpty(message = "Zipcode must not be null")
        @Size(min = 5, max = 10, message = "Zipcode must be between 5 and 10 digits")
        String zipcode,

        @NotEmpty(message = "Country must not be empty")
        String country,

        @NotNull(message = "Address type must not be null")
        AddressType addressType,

        @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number format. Must be 10 digits")
        String contactPhoneNumber
)
{}
