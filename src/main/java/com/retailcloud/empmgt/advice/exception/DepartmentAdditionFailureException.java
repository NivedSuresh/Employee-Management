package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class DepartmentAdditionFailureException extends GlobalException {

    public DepartmentAdditionFailureException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
