package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class EmployeeAdditionFailureException extends GlobalException{
    public EmployeeAdditionFailureException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
