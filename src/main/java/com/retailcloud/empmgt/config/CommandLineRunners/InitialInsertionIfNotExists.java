package com.retailcloud.empmgt.config.CommandLineRunners;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.repository.BranchRepo;
import com.retailcloud.empmgt.repository.DepartmentRepo;
import com.retailcloud.empmgt.repository.EmployeeRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
public class InitialInsertionIfNotExists{

    private final EmployeeRepo employeeRepo;

    @PostConstruct
    public void run() {

        log.warn("Initial Employee Insertion has been triggered. Ensure the project is not running in production.");

        if(this.employeeRepo.existsById(1L)){
            return;
        }

        LocalDate date = LocalDate.now().minusYears(50);


        Employee employee = Employee.builder()
                .employeeId(1L)
                .department(null)
                .dob(date)
                .firstName("Default")
                .middleName("Chief")
                .lastName("Access")
                .employeeJoinDate(date)
                .role(Role.COMPLETE_AUTHORITY)
                .exitDate(null)
                .yearlyBonusPercentage(null)
                .paidSalaries(null)
                .reportingManager(null)
                .personalAddress(null)
                .branch(null)
                .build();

        this.employeeRepo.save(employee);
    }
}
