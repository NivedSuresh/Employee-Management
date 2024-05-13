package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends GlobalException{
    public RoleNotFoundException() {
        super("Role not found!", HttpStatus.NOT_FOUND);
    }
}
