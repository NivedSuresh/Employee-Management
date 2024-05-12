package com.retailcloud.empmgt.model.payload;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NewBranch(
        @NotEmpty(message = "Please provide a building name!")
        String buildingName,

        @NotEmpty(message = "Please provide a street!")
        String street,

        @NotEmpty(message = "Please provide a city!")
        String city,

        @NotEmpty(message = "Please provide a state!")
        String state,

        @Size(min = 6, max = 6, message = "A zipcode should consist of exactly 6 numbers!")
        String zipcode,

        @NotEmpty(message = "Please provide a country!")
        String country,

        @Size(min = 10, max = 10, message = "A phone number should consist of exactly 10 numbers, excluding the country code if provided!")
        String phoneNumber,

        @Email(message = "Invalid email format provided, please provide a valid email!")
        String email
) {}
