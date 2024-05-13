package com.retailcloud.empmgt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcloud.empmgt.advice.response.ErrorResponse;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class OrderedTests {

    @Autowired ObjectMapper mapper;
    @Autowired MockMvc mvc;
    private final FakerService fakerService = new FakerService();

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");



    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.profiles.active", () -> "test");
    }



    @Order(1)
    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }


    /**
    * Branch Id generated will be 2 as a default branch is being generated
    * by @{@link com.retailcloud.empmgt.config.CommandLineRunners.InitialInsertionIfNotExists}
    * */
    @Test
    @Order(2)
    void addBranchTest() throws Exception {


        NewBranch newBranch = new NewBranch(
                "UL CyberPark",
                "Cyber Park Road",
                "Ummalathoor",
                "Kerala",
                "673008",
                "India",
                "0987654321",
                "info@retailcloudcalicut.com"
        );

        final String newBranchJSON = this.mapper.writeValueAsString(newBranch);

        this.mvc.perform(MockMvcRequestBuilders.post("/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBranchJSON))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    BranchDto branchDto = mapper.readValue(contentAsString, BranchDto.class);

                    Assertions.assertNotNull(branchDto.branchId());
                    Assertions.assertEquals(branchDto.zipcode(), newBranch.zipcode());
                });


        /*
        * Fail second addition as the branch pointing to the zipcode 673008 already exists.
        * */
        this.mvc.perform(MockMvcRequestBuilders.post("/branch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBranchJSON))
                .andExpect(status().isBadRequest());


    }



    @Test
    @Order(3)
    public void failAddDepartment() throws Exception {


        NewDepartment newDepartment1 = new NewDepartment(
                "IT",
                null,
                true, /* should be false */
                1L
        );

        final String newDeptJSON = mapper.writeValueAsString(newDepartment1);

        /* *
        * Should fail as department cannot be activated without a dept head.
        * */
        this.mvc.perform(MockMvcRequestBuilders.post("/department")
                        .header(HttpHeaders.AUTHORIZATION, "1") /* 1 because */
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDeptJSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);
                    Assertions.assertNotNull(errorResponse.message());

                    /* TODO: ???SHOULD BE REMOVED AS MESSAGES MIGHT CHANGE IN FUTURE??? */
                    Assertions.assertEquals("Department cannot be activated with out a department head, recreate again with a department head!", errorResponse.message());
                });

    }


    /** Department Id generated will be 1  **/
    @Test
    @Order(4)
    public void succeedAddDepartment() throws Exception
    {
        NewDepartment newDepartment1 = new NewDepartment(
                "IT",
                null,
                false,
                1L
        );

        final String newDeptJSON = mapper.writeValueAsString(newDepartment1);


        this.mvc.perform(MockMvcRequestBuilders.post("/department")
                        .header(HttpHeaders.AUTHORIZATION, "1") /* 1 because */
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDeptJSON))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    DepartmentDto departmentDto = mapper.readValue(contentAsString, DepartmentDto.class);
                    Assertions.assertTrue(departmentDto.getDeptHeadFullName() == null);
                    Assertions.assertEquals(departmentDto.getIsActive(), false);
                    Assertions.assertEquals(departmentDto.getCreationDate(), LocalDate.now());
                    Assertions.assertEquals(0, departmentDto.getEmployeeCount());
                });
    }

    /**
     * Employee Id generated will be 2 as a default employee is being generated
     * by @{@link com.retailcloud.empmgt.config.CommandLineRunners.InitialInsertionIfNotExists}
     * */
    @Test
    @Order(5)
    public void succeedAddEmployee() throws Exception {

        /* Add COO */
        final NewEmployee newCoo = this.fakerService.getNewEmployee(
                Role.CHIEF_OPERATING_OFFICER,
                null, /* COO won't have another reporting manager as he's the top level employee */
                null, /* BranchId = null  as COO shouldn't be associated with a specific branch but the whole institution. */
                null /* DeptId = null. Shouldn't assign COO a department. Only employees <= DEPT HEAD can be assigned to a department. */
                );

        this.mvc.perform(MockMvcRequestBuilders.post("/employee")
                .header(HttpHeaders.AUTHORIZATION, "1") //EmployeeId of Default Employee with all perms.
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newCoo)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(employeeDto.getRole(), Role.CHIEF_OPERATING_OFFICER);
                    /* COO won't be associated with a single department */
                    Assertions.assertNull(employeeDto.getDepartment());

                    /* COO won't have another reporting manager as he's the top level employee */
                    Assertions.assertNull(employeeDto.getReportingManager());
                });
    }

    @Test
    @Order(6)
    public void addEmployeeManager() throws Exception {
        final NewEmployee newBranchManager = this.fakerService.getNewEmployee(
                Role.BRANCH_MANAGER,
                2L, /* is associated with coo that was created from method 5 */
                1L, /* is associated with the branch id that was created from method 2 */
                null /* Shouldn't be associated with a particular department */
        );


        this.mvc.perform(MockMvcRequestBuilders.post("/employee")
                        .header(HttpHeaders.AUTHORIZATION, "2") //EmployeeId of the recently created coo from method 5
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBranchManager)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(employeeDto.getRole(), Role.BRANCH_MANAGER);
                    /* Branch manager won't be associated with a single department */
                    Assertions.assertNull(employeeDto.getDepartment());

                    /* Manager should be associated with recently created coo */
                    Assertions.assertNotNull(employeeDto.getReportingManager());
                    /* COO which was created from method 5*/
                    Assertions.assertEquals(employeeDto.getReportingManager().getEmployeeId(), 2L);
                    /* Manager should report to the COO */
                    Assertions.assertEquals(Role.CHIEF_OPERATING_OFFICER, employeeDto.getReportingManager().getRole());
                });
    }




    /**
     * Method should fail as the manager shouldn't be allowed to add an employee with an authority
     * which is above his own scope.
     * */
    @Test
    @Order(7)
    public void failAddCoo() throws Exception {

        final NewEmployee newBranchManager = this.fakerService.getNewEmployee(
                Role.CHIEF_OPERATING_OFFICER,
                null,
                null,
                null
        );


        this.mvc.perform(MockMvcRequestBuilders.post("/employee")
                        .header(HttpHeaders.AUTHORIZATION, "3") //EmployeeId of the recently created branch manager from method 6
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBranchManager)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = mapper.readValue(contentAsString, ErrorResponse.class);

                    /* TODO: should be replaced with an error code rather than message as error messages might change in future. */
                    Assertions.assertEquals(errorResponse.message(), "Authenticated user doesn't have necessary permissions to assign this role to an employee!!");
                });
    }


    @Test
    @Order(8)
    public void addDepartmentHead() throws Exception{
        final NewEmployee newBranchManager = this.fakerService.getNewEmployee(
                Role.BRANCH_DEPARTMENT_HEAD,
                3L, /* Refers to the Branch Manager that was successfully added from method 6 */
                1L,  /* Refers to the branch that was added from method 2 */
                1L /* Refers to the dept that was created from method 4 */
        );


        this.mvc.perform(MockMvcRequestBuilders.post("/employee")
                        .header(HttpHeaders.AUTHORIZATION, "3") //EmployeeId of the recently created branch manager from method 6
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBranchManager)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(employeeDto.getDepartment().getDeptId(), 1L);
                    Assertions.assertEquals(employeeDto.getDepartment().getDeptHeadId(), employeeDto.getEmployeeId());
                });
    }



}
