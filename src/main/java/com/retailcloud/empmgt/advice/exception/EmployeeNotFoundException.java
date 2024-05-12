package com.retailcloud.empmgt.advice.exception;

import com.retailcloud.empmgt.model.entity.enums.Role;
import org.springframework.http.HttpStatus;

public class EmployeeNotFoundException extends GlobalException {
    public EmployeeNotFoundException(Role role) {
        super(
                role.name().concat(" not found!"),
                HttpStatus.NOT_FOUND
        );
    }

    public EmployeeNotFoundException(String message) {
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
