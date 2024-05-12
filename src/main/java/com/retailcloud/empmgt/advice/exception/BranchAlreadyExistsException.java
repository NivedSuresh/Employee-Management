package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class BranchAlreadyExistsException extends GlobalException{
    public BranchAlreadyExistsException(String zipcode) {
        super("A branch already exists for the zipcode: ".concat(zipcode), HttpStatus.BAD_REQUEST);
    }
}
