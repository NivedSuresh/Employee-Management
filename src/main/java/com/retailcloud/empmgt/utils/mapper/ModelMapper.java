package com.retailcloud.empmgt.utils.mapper;

import com.retailcloud.empmgt.model.entity.Address;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.BranchDto;
import com.retailcloud.empmgt.model.payload.DepartmentDto;
import com.retailcloud.empmgt.model.payload.EmployeeDto;
import com.retailcloud.empmgt.model.payload.NewAddress;

public class ModelMapper {
    public static DepartmentDto toDto(Department department) {
        if(department == null) return null;
        return DepartmentDto.builder()
                .deptId(department.getDeptId())
                .isActive(department.getIsActive())
                .creationDate(department.getCreationDate())
                .branch(toDto(department.getBranch()))
                .deptHeadFullName(getEmployeeFullName(department.getDeptHead()))
                .employeeCount(department.getEmployeeCount())
                .build();
    }

    private static String getEmployeeFullName(Employee employee) {
        return employee == null ? null : employee.getFirstName().concat(" ").concat(employee.getLastName());
    }

    public static BranchDto toDto(Branch branch) {
        if(branch == null) return  null;
        return BranchDto.builder()
                .branchId(branch.getBranchId())
                .buildingName(branch.getBuildingName())
                .street(branch.getStreet())
                .city(branch.getCity())
                .country(branch.getCountry())
                .email(branch.getEmail())
                .phoneNumber(branch.getPhoneNumber())
                .zipcode(branch.getZipcode())
                .state(branch.getState())
                .build();
    }


    public static EmployeeDto toDto(Employee employee, boolean appendReportingManager){
        if(employee == null) return null;

        final EmployeeDto reportingManager = appendReportingManager ? toDto(employee.getReportingManager(), false) : null;

        return EmployeeDto.builder()
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastName(employee.getLastName())
                .dob(employee.getDob())
                .employeeJoinDate(employee.getEmployeeJoinDate())
                .department(toDto(employee.getDepartment()))
                .role(employee.getRole())
                .yearlyBonusPercentage(employee.getYearlyBonusPercentage())
                .exitDate(employee.getExitDate())
                .reportingManager(reportingManager)
                .build();
    }

    public static Address toEntity(NewAddress newAddress, boolean includeId) {
        return Address.builder()
                .addressId(includeId ? newAddress.addressId() : null)
                .city(newAddress.city())
                .state(newAddress.state())
                .street(newAddress.street())
                .addressType(newAddress.addressType())
                .buildingName(newAddress.buildingName())
                .contactPhoneNumber(newAddress.contactPhoneNumber())
                .country(newAddress.country())
                .zipcode(newAddress.zipcode())
                .build();
    }
}
