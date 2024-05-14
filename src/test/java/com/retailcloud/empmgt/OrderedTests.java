package com.retailcloud.empmgt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.Message;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.entity.enums.Role;
import com.retailcloud.empmgt.model.payload.*;
import com.retailcloud.empmgt.service.Branch.BranchService;
import com.retailcloud.empmgt.service.Employee.EmployeeService;
import com.retailcloud.empmgt.service.FetchService;
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
import org.springframework.test.web.servlet.ResultActions;
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
    @Autowired BranchService branchService;
    @Autowired FetchService fetchService;
    @Autowired EmployeeService employeeService;
    private final FakerService fakerService = new FakerService();

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.profiles.active", () -> "test");
    }





    /**
     <P></P>

     IMPORTANT: TESTS SHOULD BE RUN AS A WHOLE AND NOT INDIVIDUALLY.
     TESTS WRITTEN IN THIS CLASS DEPEND ON EACH OTHER AND ARE ORDERED.
     ORDERED TESTS WERE WRITTEN TO REPLICATE POSTMAN BUT IN A BETTER WAY.
     PLEASE RUN THE TEST CLASS AS A WHOLE AND NOT AS INDIVIDUAL METHODS.

     <p></p>

     IMPORTANT: TESTS SHOULD BE RUN AS A WHOLE AND NOT INDIVIDUALLY.
     TESTS WRITTEN IN THIS CLASS DEPEND ON EACH OTHER AND ARE ORDERED.
     ORDERED TESTS WERE WRITTEN TO REPLICATE POSTMAN BUT IN A BETTER WAY.
     PLEASE RUN THE TEST CLASS AS A WHOLE AND NOT AS INDIVIDUAL METHODS.

     <p></p>

     IMPORTANT: TESTS SHOULD BE RUN AS A WHOLE AND NOT INDIVIDUALLY.
     TESTS WRITTEN IN THIS CLASS DEPEND ON EACH OTHER AND ARE ORDERED.
     ORDERED TESTS WERE WRITTEN TO REPLICATE POSTMAN BUT IN A BETTER WAY.
     PLEASE RUN THE TEST CLASS AS A WHOLE AND NOT AS INDIVIDUAL METHODS.

     <p></p>

     IMPORTANT: TESTS SHOULD BE RUN AS A WHOLE AND NOT INDIVIDUALLY.
     TESTS WRITTEN IN THIS CLASS DEPEND ON EACH OTHER AND ARE ORDERED.
     ORDERED TESTS WERE WRITTEN TO REPLICATE POSTMAN BUT IN A BETTER WAY.
     PLEASE RUN THE TEST CLASS AS A WHOLE AND NOT AS INDIVIDUAL METHODS.

     <p></p>
     */





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
                true, /* activate department: should be false */
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
                    Message message = mapper.readValue(contentAsString, Message.class);
                    Assertions.assertNotNull(message.message());

                    /* TODO: ???SHOULD BE REMOVED AS MESSAGES MIGHT CHANGE IN FUTURE??? */
                    Assertions.assertEquals("Department cannot be activated with out a department head, recreate again with a department head!", message.message());
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


        this.performPost("/department", "1", newDeptJSON) /* Principal id 1 as the default employee initialized during startup will have the id 1 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    DepartmentDto departmentDto = mapper.readValue(contentAsString, DepartmentDto.class);
                    Assertions.assertNull(departmentDto.getDeptHeadFullName());
                    Assertions.assertEquals(departmentDto.getIsActive(), false);
                    Assertions.assertEquals(departmentDto.getCreationDate(), LocalDate.now());
                });
    }

    /**
     * Employee Id generated will be 2 as a default employee is being generated
     * by @{@link com.retailcloud.empmgt.config.CommandLineRunners.InitialInsertionIfNotExists}
     * */
    @Test
    @Order(5)
    public void succeedAddCoo() throws Exception {

        /* Add COO */
        final NewEmployee newCoo = this.fakerService.getNewEmployee(
                Role.CHIEF_OPERATING_OFFICER,
                null, /* COO won't have another reporting manager as he's the top level employee */
                null, /* BranchId = null  as COO shouldn't be associated with a specific branch but the whole institution. */
                null /* DeptId = null. Shouldn't assign COO a department. Only employees <= DEPT HEAD can be assigned to a department. */
                );


        this.performPost("/employee", "1", mapper.writeValueAsString(newCoo)) /* Principal id 1 as the default employee initialized during startup will have the id 1 and has necessary permissions to add coo */
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


    /**
     * The branch manager being added will have an id of 3
     * */
    @Test
    @Order(6)
    public void addBranchManager() throws Exception {
        final NewEmployee newBranchManager = this.fakerService.getNewEmployee(
                Role.BRANCH_MANAGER,
                2L, /* is associated with coo that was created from method 5 */
                1L, /* is associated with the branch id that was created from method 2 */
                null /* Shouldn't be associated with a particular department */
        );


        this.performPost("/employee", "2", mapper.writeValueAsString(newBranchManager)) /* Principal id 2 points to the coo registered from method 5 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(Role.BRANCH_MANAGER, employeeDto.getRole());
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


        this.performPost("/employee", "3", mapper.writeValueAsString(newBranchManager)) /* Principal id 3 as the manager registered above on method 6 */
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    Message message = mapper.readValue(contentAsString, Message.class);

                    /* TODO: should be replaced with an error code rather than message as error messages might change in future. */
                    Assertions.assertEquals(message.message(), "Authenticated user doesn't have necessary permissions to assign this role to an employee!!");
                });
    }



    /**
    * Employee Id here will be 4 (Dept Head) as this is the third successful
    * insertion of these ordered tests.
    * */
    @Test
    @Order(8)
    public void addDepartmentHead() throws Exception{
        final NewEmployee newDeptHead = this.fakerService.getNewEmployee(
                Role.BRANCH_DEPARTMENT_HEAD,
                3L, /* Refers to the Branch Manager that was successfully added from method 6 */
                1L,  /* Refers to the branch that was added from method 2 */
                1L /* Refers to the dept that was created from method 4 */
        );


        this.performPost("/employee", "3", mapper.writeValueAsString(newDeptHead)) /* Principal id 3 as the manager registered above on method 6 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    /* Validate department assigned */
                    Assertions.assertEquals(employeeDto.getDepartment().getDeptId(), 1L);
                    /* Checking if the employee we added here is as the dept head is persisted in department entity as well */
                    Assertions.assertEquals(employeeDto.getDepartment().getDeptHeadId(), employeeDto.getEmployeeId());

                    /*Checking if the reporting manager's id is pointing to the branch manager's id (ie id generated from method 6)*/
                    Assertions.assertEquals(employeeDto.getReportingManager().getEmployeeId(), 3L);
                });
    }


    /** Team lead inserted here will have the id 5 * */
    @Test
    @Order(9)
    public void addTeamLead() throws Exception {
        final NewEmployee newTeamLead = this.fakerService.getNewEmployee(
                Role.TEAM_LEAD,
                4L, /* Refers to the Dept Head that was successfully added from method 8 */
                1L,  /* Refers to the branch that was added from method 2 */
                1L /* Refers to the dept that was created from method 4 */
        );

        String valueAsString = mapper.writeValueAsString(newTeamLead);
        this.performPost("/employee", "3", valueAsString) /* Principal id 3 as the manager registered above on method 6 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = mapper.readValue(contentAsString, EmployeeDto.class);

                    /* Validate department assigned */
                    Assertions.assertEquals(employeeDto.getDepartment().getDeptId(), 1L);
                    /*Checking if the reporting manager's id is pointing to the dept head's id (ie id generated from method 8)*/
                    Assertions.assertEquals(employeeDto.getReportingManager().getEmployeeId(), 4L);
                });

    }



    /**
     * The method here will add the employee with a wrong department head id,
     * the id provided will be of the branch manager. but as per application logic
     * the team lead should report to a dept head.
     * <p></p>
     * Thus, application should return the employee with the id of recently
     * created dept head from method 8. (dept head id = 4)
     * <p></p>
     * Team lead inserted here will have the Id 6.
     * */
    @Test
    @Order(10)
    public void AddTeamLeadWithWrongReportingManager() throws Exception {
        final NewEmployee newTeamLead = this.fakerService.getNewEmployee(
                Role.TEAM_LEAD,
                3L, /* Refers to the Branch manager that was successfully added from method 6 */
                1L,  /* Refers to the branch that was added from method 2 */
                1L /* Refers to the dept that was created from method 4 */
        );

        String valueAsString = mapper.writeValueAsString(newTeamLead);
        this.performPost("/employee", "3", valueAsString) /* Principal id 3 as the manager registered above on method 6 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = this.mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(employeeDto.getRole(), Role.TEAM_LEAD);
                    Assertions.assertEquals(employeeDto.getReportingManager().getRole(), Role.BRANCH_DEPARTMENT_HEAD);
                    Assertions.assertEquals(employeeDto.getReportingManager().getEmployeeId(), 4);
                });

    }



    /**
     * Junior assistant that is being added here will have the id 7
     * */
    @Test
    @Order(11)
    public void insertJuniorAssistant() throws Exception {
        final NewEmployee newTeamLead = this.fakerService.getNewEmployee(
                Role.JUNIOR_ASSISTANT,
                6L, /* Refers to the Team Lead that was successfully added from method 6 */
                1L,  /* Refers to the branch that was added from method 2 */
                1L /* Refers to the dept that was created from method 4 */
        );

        String valueAsString = mapper.writeValueAsString(newTeamLead);
        this.performPost("/employee", "6", valueAsString) /* Principal id 6 as the lead registered above on method 10 */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    EmployeeDto employeeDto = this.mapper.readValue(contentAsString, EmployeeDto.class);

                    Assertions.assertEquals(employeeDto.getRole(), Role.JUNIOR_ASSISTANT);
                    Assertions.assertEquals(employeeDto.getReportingManager().getRole(), Role.TEAM_LEAD);
                    Assertions.assertEquals(employeeDto.getReportingManager().getEmployeeId(), 6);

                    /* Validate dept head just to be sure */
                    Assertions.assertEquals(employeeDto.getDepartment().getDeptHeadId(), 4);
                    Assertions.assertNotNull(employeeDto.getBranch());
                });
    }



    /**
     * Should fail as the existing branch manager(id = 3) is managing a different branch
     * than the one that is being created inside this method.
     * */
    @Test
    @Order(12)
    public void failAddDepartment2() throws Exception
    {

        NewBranch newBranch = new NewBranch(
                "Random",
                "street",
                "city",
                "Kerala",
                "6730010",
                "India",
                "0987654329",
                "info@gmail.com"
        );

        Branch branch = this.branchService.addBranch(newBranch);

        NewDepartment newDepartment1 = new NewDepartment(
                "DEVOPS",
                null,
                false,
                branch.getBranchId()
        );

        final String newDeptJSON = mapper.writeValueAsString(newDepartment1);


        this.performPost("/department", "3", newDeptJSON) /* Principal id 3 as the previously initialized manager */
                .andExpect(status().isNotAcceptable())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    Message message = mapper.readValue(contentAsString, Message.class);

                    Assertions.assertEquals(message.message(), "User doesn't have the necessary permissions to create a department in a branch other than their own branch.");
                });
    }


    @Test
    @Order(13)
    public void updateDepartmentHead() throws Exception {

        /* Assigning new head to the department. The id will point to the recently
        * created team lead from method 10  */
        EmployeeDepartmentUpdate update = new EmployeeDepartmentUpdate(1L, 6L);

        Department department = this.fetchService.findDepartmentByIdElseThrow(1L, "failed test as previously created department not found!");

        Employee prevHead = department.getDeptHead();

        final Employee finalPrevHead = prevHead;
        this.mvc.perform(MockMvcRequestBuilders.put("/department/head")
                .header(HttpHeaders.AUTHORIZATION, 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(update)))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    DepartmentDto departmentDto = mapper.readValue(contentAsString, DepartmentDto.class);

                    Assertions.assertEquals(departmentDto.getDeptId(), update.deptId());
                    Assertions.assertNotEquals(departmentDto.getDeptHeadId(), finalPrevHead.getEmployeeId());
                    Assertions.assertEquals(departmentDto.getDeptHeadId(), update.movableEmployeeId());
                    Assertions.assertEquals(departmentDto.getIsActive(), true);

                    Employee newHead = this.fetchService.findEmployeeByIdElseThrow(departmentDto.getDeptHeadId(), "Failed to find new dept head from test!");

                    /* Ensure if the new heads reporting manager is updated and is now pointing to the branch manager */
                    Assertions.assertEquals(newHead.getReportingManager().getEmployeeId(), 3L);


                    /* Fetch all Team leads from the department and see if those employees are reporting to the new head */
                    this.fetchService.findByRoleAndDepartmentElseEmpty(Role.TEAM_LEAD, department, null)
                            .forEach(employee -> {
                                Assertions.assertEquals(employee.getReportingManager().getEmployeeId(), newHead.getEmployeeId());
                            });


                });

        prevHead = this.fetchService.findEmployeeByIdElseThrow(prevHead.getEmployeeId(), "Failed to find prev head from test case!");
        /* Verify updates on previous department head */
        Assertions.assertNull(prevHead.getDepartment());
        Assertions.assertEquals(prevHead.getRole(), Role.UNDEFINED);
        Assertions.assertNull(prevHead.getReportingManager());

    }



    /* This operation should fail as department has employees assigned to it */
    @Test
    @Order(14)
    public void failDeleteDepartment() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.delete("/department/1")
                .header(HttpHeaders.AUTHORIZATION, 3))
                .andExpect(status().isNotAcceptable())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    Message message = mapper.readValue(contentAsString, Message.class);

                    /*  Todo : replace with error code as messages might change in future */
                    Assertions.assertEquals("Cannot delete department as there are employees assigned to this department!", message.message());
                });
    }


    @Test
    @Order(15)
    public void addDevops() throws Exception {
        NewDepartment newDepartment1 = new NewDepartment(
                "DEVOPS",
                4L,
                true,
                1L
        );

        final String newDeptJSON = mapper.writeValueAsString(newDepartment1);


        this.performPost("/department", "3", newDeptJSON) /* Principal id 3 as the previously initialized manager */
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    DepartmentDto departmentDto = mapper.readValue(contentAsString, DepartmentDto.class);

                    Assertions.assertEquals(departmentDto.getDeptHeadId(), 4L);

                    Employee deptHead = this.fetchService.findEmployeeByIdElseThrow(4L, "");

                    Assertions.assertNotNull(deptHead.getDepartment());
                    Assertions.assertEquals(departmentDto.getDeptId(), deptHead.getDepartment().getDeptId());
                    Assertions.assertEquals(departmentDto.getDeptHeadId(), deptHead.getEmployeeId());
                    Assertions.assertEquals(deptHead.getBranch().getBranchId(), departmentDto.getBranch().branchId());
                });
    }


    /**
     Employee Information till now:          <br>
     ______________________________
     <p></p>
     Default User -> (ID - 1) (Created on application initialization if profile contains 'sim/test/dev').
     <br>
     COO User -> (ID - 2).
     <p></p>
     Branch Manager -> (ID - 3) is managing Branch ID 1.
     <p></p>
     Department Head -> (ID - 4), previously Department Head for Department 1
     but the role was taken over by (ID - 6). Currently, Department Head for Department 2
     <p></p>
     Team Lead -> (ID - 5), part of Department 1.
     <p></p>
     Department Head -> (ID - 6), part of Department 1, took over the role from (ID - 4).
     <p></p>
     Junior Assistant -> (ID - 7), part of Department 1.

     <p></p><p></p>
     Available departments = IT with ID 1, Devops with ID 2
     <p></p>
     Available branch = Calicut branch with ID 1

     <p></p>
     IMPORTANT: RUN TEST CASES AS WHOLE (CLASS) AND NOT AS INDIVIDUAL METHODS
    */



    /**
     * Tests will be written without MockMvc from now onwards for better readability
     * */
    @Test
    @Order(16)
    public void moveAllEmployeesToDepartment2(){

        /*
        * 5 is team lead.
        * 6 is dept head.
        * 7 is junior assistant.
        * */
        long[] allEmployeesInDepartment1 = {5, 6, 7};

        for(long empId : allEmployeesInDepartment1){
            EmployeeDepartmentUpdate update = new EmployeeDepartmentUpdate(2L, empId);
            this.employeeService.moveEmployeeToDepartment(update, 3L);
        }
    }


    private ResultActions performPost(String endpoint, String principalId, String json) throws Exception {
        return this.mvc.perform(MockMvcRequestBuilders.post(endpoint)
                .header(HttpHeaders.AUTHORIZATION, principalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }



}
