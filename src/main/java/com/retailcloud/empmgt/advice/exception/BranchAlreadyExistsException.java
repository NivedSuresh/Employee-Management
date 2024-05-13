package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class BranchAlreadyExistsException extends GlobalException{
    public BranchAlreadyExistsException() {
        super("A branch already exists with the provided zipcode/phone number/email. All three of these fields should be unique!", HttpStatus.BAD_REQUEST);
    }
}
