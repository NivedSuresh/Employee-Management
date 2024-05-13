package com.retailcloud.empmgt.config.CommandLineRunners;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.repository.BranchRepo;
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
    private final BranchRepo branchRepo;

    @Override
    public void run(String... args) {

        log.warn("Initial Employee Insertion has been triggered. Ensure the project is not running in production.");

        if(this.employeeRepo.existsById(1L)){
            return;
        }

        LocalDate date = LocalDate.now().minusYears(50);


        /* Default branch */
//        Branch branch = this.branchRepo.findById(1L).orElse(
////                this.branchRepo.save(
//                        Branch.builder()
//                        .buildingName("Retail Cloud Default")
//                        .zipcode("ALL")
//                        .country("ALL")
//                        .street("ALL")
//                        .city("ALL")
//                        .state("ALL")
//                        .email("info@retailcloud.com")
//                        .phoneNumber("NONE")
//                        .build()
////        )
//        );

        /* Dept will be activated here as dept is directly being saved to database. */
//        Department department = departmentRepo.findById(1L)
//                .orElse(
//                        departmentRepo.save(
//                        Department.builder()
//                            .deptId(Long.MAX_VALUE)
//                            .deptName("DEFAULT_DEPT")
//                            .deptHead(null)
//                            .creationDate(date)
//                            .isActive(true)
//                            .branch(branch)
//                            .build()
//                ));


        Employee employee = Employee.builder()
                .employeeId(1L)
                .department(null)
                .dob(date)
                .firstName("Default")
                .middleName("Chief")
                .lastName("Access")
                .employeeJoinDate(date)
                .role(Role.INITIAL_CHIEF_ACCESS)
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
