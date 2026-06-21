package com.github.elja9y.todo.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {
    public final String errorCode;
    public final HttpStatus status;

    public AppException(String message, String errorCode, HttpStatus status) {
        super(message);   // AppException passes message up to RuntimeException and stores errorCode and status itself.
                          // RuntimeException only cares about message — that's its constructor parameter, used for getMessage().
        this.errorCode = errorCode;
        this.status = status;
    }
}
