package com.retailcloud.empmgt.controller;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.EmployeeDto;
import com.retailcloud.empmgt.model.payload.NewEmployee;
import com.retailcloud.empmgt.service.Employee.EmployeeService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import com.retailcloud.empmgt.utils.validation.PayloadValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


/**
 * Context Path = /api/management
 * <p></p>
 * Management endpoint's should be restricted from user's with out a management role.
 * ie devs, ops etc
 */
@RequiredArgsConstructor
@RequestMapping("/employee")
@RestController
public class EmployeeController {


    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;


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



    @PostMapping
    public ResponseEntity<EmployeeDto> addEmployee(@Validated @RequestBody final NewEmployee newEmployee,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) final Long principalId)
    {
        final Employee employee = this.employeeService.addEmployee(newEmployee, principalId);
        EmployeeDto dto = modelMapper.toDto(employee, true, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


}
