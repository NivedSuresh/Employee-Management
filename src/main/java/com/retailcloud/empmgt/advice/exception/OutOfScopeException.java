package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class OutOfScopeException extends GlobalException {

    public OutOfScopeException(String message){
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
