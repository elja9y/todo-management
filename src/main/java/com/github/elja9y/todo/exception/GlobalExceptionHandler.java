package com.github.elja9y.todo.exception;


import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle module-specific exceptions: The exception handler intercepts
    // Exceptions before going too client, and instead, it builds a well-formed one
    // It returns a response type ProblemDetails, which has:
    // 1) The error date 2) The error message itself
    // 3) The endpoint URI fetched from current http context 4) the suiting status code

    // Handles all EntityExceptions
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDetails> handleAccountException(AppException exception,
                                                               WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                request.getDescription(false),
                exception.errorCode
        );

        return new ResponseEntity<>(errorDetails, exception.status);
    }

    // Handle general non module specific exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception e,
                                                              WebRequest request){
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}