package com.retailcloud.empmgt.utils.mapper;

import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.entity.Address;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.BranchDto;
import com.retailcloud.empmgt.model.payload.DepartmentDto;
import com.retailcloud.empmgt.model.payload.EmployeeDto;
import com.retailcloud.empmgt.model.payload.NewAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ModelMapper {


    private final CompanyRoles companyRoles;

    public static DepartmentDto toDto(Department department) {
        if(department == null) return null;
        Long deptHeadId = null;
        if(department.getDeptHead() != null){
            deptHeadId = department.getDeptHead().getEmployeeId();
        }
        return DepartmentDto.builder()
                .deptHeadId(deptHeadId)
                .deptName(department.getDeptName())
                .deptId(department.getDeptId())
                .isActive(department.getIsActive())
                .creationDate(department.getCreationDate())
                .branch(toDto(department.getBranch()))
                .deptHeadFullName(getEmployeeFullName(department.getDeptHead()))
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



    public EmployeeDto toDto(Employee employee, boolean appendReportingManager, boolean fetchBranch){
        if(employee == null) return null;

        final EmployeeDto reportingManager = appendReportingManager ? toDto(employee.getReportingManager(), false, false) : null;


        BranchDto branch = null;

        if(companyRoles.noBranchRequired().contains(employee.getRole())){
            branch = null;
        }
        else if (employee.getDepartment() != null)
        {
            branch = toDto(employee.getDepartment().getBranch());
        }
        else if(reportingManager != null && !companyRoles.noBranchRequired().contains(reportingManager.getRole()))
        {
            branch = reportingManager.getDepartment().getBranch();
        }
        else if(fetchBranch)
        {
            branch = toDto(employee.getBranch());
        }


        return EmployeeDto.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastName(employee.getLastName())
                .dob(employee.getDob())
                .employeeJoinDate(employee.getEmployeeJoinDate())
                .department(toDto(employee.getDepartment()))
                .role(employee.getRole())
                .yearlyBonusPercentage(employee.getYearlyBonusPercentage())
                .salary(employee.getSalary())
                .exitDate(employee.getExitDate())
                .reportingManager(reportingManager)
                .branch(branch)
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
