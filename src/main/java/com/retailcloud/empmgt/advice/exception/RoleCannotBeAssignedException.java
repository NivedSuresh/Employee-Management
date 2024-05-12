package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class RoleCannotBeAssignedException extends GlobalException{
    public RoleCannotBeAssignedException() {
        super(
                "Authenticated user doesn't have necessary permissions to assign this role to an employee!!",
                HttpStatus.BAD_REQUEST
        );
    }
}
