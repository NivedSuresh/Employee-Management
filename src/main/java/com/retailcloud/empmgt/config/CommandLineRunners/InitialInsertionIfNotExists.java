package com.retailcloud.empmgt.config.CommandLineRunners;

import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;




/**
 * Represents an Employee with elevated privileges typically inserted during application initialization.
 * This is crucial because creating an Employee usually requires association with a Department,
 * and creating a Department often necessitates specific permissions.
 *<p></p>
 * For example, the authenticated user or a management-level Employee must either have a chief role
 * or be part of the same branch to create departments. Management-level Employees who are not chief officers
 * cannot create departments outside their scope (i.e., in a different branch).
 * <p></p>
 * NB: The initialization of this Employee only occurs if the application is running on the 'sim/test/dev' profile.
 */
@RequiredArgsConstructor
@Profile({"sim", "test", "dev"})
@Slf4j
@Component
public class InitialInsertionIfNotExists implements CommandLineRunner {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;

    @Override
    public void run(String... args) {

        log.warn("Profile has either dev, sim or test in it, thus Initial Employee Insertion is triggered.");

        if(this.employeeRepo.existsById(1L)){
            return;
        }

        LocalDate date = LocalDate.now().minusYears(50);

        /*
        * Dept will be activated here as dept is directly being saved to database.
        * */
        Department department = departmentRepo.findById(1L).orElse(departmentRepo.save(
                Department.builder()
                        .deptId(Long.MAX_VALUE)
                        .deptName("CHIEF_OFFICERS_DEPT")
                        .deptHead(null)
                        .creationDate(date)
                        .isActive(true)
                        .branch(null)
                        .build()
        ));


        Employee employee = Employee.builder()
                .employeeId(1L)
                .department(department)
                .dob(date)
                .firstName("Default")
                .middleName("Chief")
                .lastName("Access")
                .employeeJoinDate(date)
                .branchDetails(null)
                .role(Role.DEFAULT_CHIEF_ACCESS)
                .exitDate(null)
                .yearlyBonusPercentage(null)
                .paidSalaries(null)
                .reportingManager(null)
                .personalAddress(null)
                .build();

        System.out.println("EmpID: " + this.employeeRepo.save(employee).getEmployeeId());
    }
}
