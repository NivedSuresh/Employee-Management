package com.retailcloud.empmgt.service.Employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcloud.empmgt.advice.exception.EntityOperationFailureException;
import com.retailcloud.empmgt.model.entity.*;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.NewEmployee;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import com.retailcloud.empmgt.service.FetchService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@Service
class EmployeeServiceImpl implements EmployeeService
{


    private final EmployeeRepo employeeRepo;
    private final FetchService fetchService;


    public Employee addEmployee(final NewEmployee newEmployee, final Long principalId)
    {
        final Department department = newEmployee.departmentId() != null ? fetchService.findDepartmentByIdElseThrow(newEmployee.departmentId(), "Operation failed: Failed to find department"): null;

        if(department != null && !Objects.equals(department.getBranch().getBranchId(), newEmployee.branchId()))
        {
            throw new EntityOperationFailureException("The department should belong to the same branch as the employee!");
        }

        final Role reportingManagerRoleTobe = newEmployee.role().getReportingManagerRole();

        final Employee reportingManager = newEmployee.reportingManagerId() == null ? null :
                this.fetchService.findEmployeeByIdAndRoleAndDepartmentElseThrow(newEmployee.reportingManagerId(), reportingManagerRoleTobe, department,  "Operation failed as reporting manager cannot be assigned!");

        final Address address = ModelMapper.toEntity(newEmployee.personalAddress(), false);

        final Employee employee = Employee.builder()
                .firstName(newEmployee.firstName())
                .middleName(newEmployee.middleName())
                .lastName(newEmployee.lastName())
                .dob(newEmployee.dob())
                .employeeJoinDate(newEmployee.employeeJoinDate())
                .department(department)
                .role(newEmployee.role())
                .yearlyBonusPercentage(newEmployee.yearlyBonusPercentage())
                .reportingManager(reportingManager)
                .personalAddress(address)
                .branchDetails(department != null ? department.getBranch() : null)
                .build();

        return this.employeeRepo.save(employee);
    }
}
