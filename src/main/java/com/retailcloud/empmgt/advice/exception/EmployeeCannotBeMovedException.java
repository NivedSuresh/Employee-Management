package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class EmployeeCannotBeMovedException extends GlobalException{
    public EmployeeCannotBeMovedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
