package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class DepartmentNotFoundException extends GlobalException{

    public DepartmentNotFoundException(String message){
        super(message, HttpStatus.NOT_FOUND);
    }
}
