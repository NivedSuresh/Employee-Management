package com.retailcloud.empmgt.controller;

import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.payload.DepartmentDto;
import com.retailcloud.empmgt.model.payload.DeptHeadUpdate;
import com.retailcloud.empmgt.model.payload.NewDepartment;
import com.retailcloud.empmgt.service.Department.DepartmentService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import com.retailcloud.empmgt.utils.validation.PayloadValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


/**
 * Context Path = /api/management
 * <p></p>
 * Management endpoint's should be restricted from user's with out a management role.
 * ie devs, ops etc
 */
@RequestMapping("/department")
@RequiredArgsConstructor
@RestController
public class DepartmentController {


    private final DepartmentService departmentService;

    /**
     * Trims white spaces from user input.
     * <p></p>
     * ie: If user input for the field 'username' only consists of white spaces
     * then it'd be trimmed down to an empty spring thus leading @{@link org.springframework.validation.BindException}.
     */
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        PayloadValidator.setCustomEditorForWebBinder(dataBinder);
    }


    /**
     * Header should be appended to the request from the Claims by gateway before
     * forwarding the request.
     */
    @PostMapping
    public ResponseEntity<DepartmentDto> addDepartment(@RequestHeader(HttpHeaders.AUTHORIZATION) final Long principalId,
                                                       @Validated @RequestBody final NewDepartment newDepartment) {
        Department department = this.departmentService.addDepartment(newDepartment, principalId);
        DepartmentDto departmentDto = ModelMapper.toDto(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentDto);
    }


    @PutMapping("/head")
    public ResponseEntity<DepartmentDto> updateDepartmentHead(@Validated @RequestBody final DeptHeadUpdate update){
        Department department = this.departmentService.assignNewHeadForDept(update);
        return ResponseEntity.accepted().body(ModelMapper.toDto(department));
    }


}
