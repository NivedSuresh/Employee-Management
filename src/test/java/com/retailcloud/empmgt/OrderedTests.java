package com.retailcloud.empmgt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcloud.empmgt.advice.response.ErrorResponse;
import com.retailcloud.empmgt.model.payload.BranchDto;
import com.retailcloud.empmgt.model.payload.DepartmentDto;
import com.retailcloud.empmgt.model.payload.NewBranch;
import com.retailcloud.empmgt.model.payload.NewDepartment;
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
                "info@retailcloud.com"
        );

        final String newBranchJSON = this.mapper.writeValueAsString(newBranch);

        this.mvc.perform(MockMvcRequestBuilders.post("/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBranchJSON))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    BranchDto branchDto = mapper.readValue(contentAsString, BranchDto.class);

                    System.out.println(branchDto);

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

}
