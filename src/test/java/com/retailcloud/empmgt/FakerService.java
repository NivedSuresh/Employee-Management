package com.retailcloud.empmgt;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.retailcloud.empmgt.model.entity.enums.AddressType;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.NewAddress;
import com.retailcloud.empmgt.model.payload.NewEmployee;

import java.time.LocalDate;

public class FakerService {

    private final Faker faker = new Faker();
    public NewAddress getAddress() {
        Address address = faker.address();
        return new NewAddress(
                null,
                address.buildingNumber(),
                address.streetName(),
                address.city(),
                address.state(),
                address.zipCode(),
                address.country(),
                AddressType.CURRENT_RESIDENCE,
                faker.phoneNumber().phoneNumber()
        );
    }

    public NewEmployee getNewEmployee(final Role role,
                                      final Long reportingMangerId,
                                      final Long branchId,
                                      final Long departmentId)
    {
        Address address = faker.address();
        final NewAddress commonAddress = this.getAddress();
        return new NewEmployee(
                address.firstName(),
                null,
                address.lastName(),
                LocalDate.now().minusYears(30).minusDays(50),
                LocalDate.now(),
                departmentId,
                role,
                10.0,
                reportingMangerId,
                commonAddress,
                branchId,
                null
        );
    }
}
