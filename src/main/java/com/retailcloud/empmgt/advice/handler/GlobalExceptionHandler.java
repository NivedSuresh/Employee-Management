package com.retailcloud.empmgt.advice.handler;

import com.retailcloud.empmgt.advice.exception.GlobalException;
import com.retailcloud.empmgt.model.payload.Message;
import com.retailcloud.empmgt.utils.validation.PayloadValidator;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Message> handleGlobalExceptions(final GlobalException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(Message.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Message> handleConnectionFailures() {
        final String message = "The service is unavailable right now, please try again after sometime.";
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Message.builder().message(message).build());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Message> handleBindException(BindException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        final String message = PayloadValidator.fetchFirstError(bindingResult);
        return ResponseEntity.badRequest().body(Message.builder().message(message).build());
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<Message> handleInvalidRequest() {
        final String message = "Cannot proceed requested as required credentials were not provided!";
        return ResponseEntity.badRequest().body(Message.builder().message(message).build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Message> handleMethodNotSupported() {
        final String message = "Invalid request made, let us know if we made a mistake!";
        return ResponseEntity.badRequest().body(Message.builder().message(message).build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Message> handleMethodArgumentTypeMismatchException() {
        final String message = "Invalid request made, let us know if we made a mistake!";
        return ResponseEntity.badRequest().body(Message.builder().message(message).build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Message> handleRest(Exception e){
        e.printStackTrace();
        final String message = "An unknown error occurred! Contact us if the issue persists!";
        return ResponseEntity.internalServerError().body(Message.builder().message(message).build());
    }
}
