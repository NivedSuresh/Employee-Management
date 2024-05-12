package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class NoAccessToCreateDepartmentException extends GlobalException {
    public NoAccessToCreateDepartmentException() {
        super(
                "User doesn't have the necessary permissions to create a department in a branch other than their own branch.",
                HttpStatus.NOT_ACCEPTABLE
        );
    }
}
