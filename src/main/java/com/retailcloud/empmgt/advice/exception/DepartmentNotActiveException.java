package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class DepartmentNotActiveException extends GlobalException{
    public DepartmentNotActiveException() {
        super("The department is not active. Make sure the department is still function and the department has a department head assigned to it.", HttpStatus.BAD_REQUEST);
    }
}
