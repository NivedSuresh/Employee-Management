package com.retailcloud.empmgt.advice.exception;

import org.springframework.http.HttpStatus;

public class BranchNotFoundException extends GlobalException {
    public BranchNotFoundException() {
        super("Unable to find the company branch!", HttpStatus.NOT_FOUND);
    }
}
