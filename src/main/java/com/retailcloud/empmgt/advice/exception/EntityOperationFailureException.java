package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class EntityOperationFailureException extends GlobalException{
    public EntityOperationFailureException(String message) {
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
