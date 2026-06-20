package com.github.elja9y.todo.exception;

import org.springframework.http.HttpStatus;

public class TodoException extends RuntimeException {
    public final String errorCode;
    public final HttpStatus status;

    public TodoException(String message, String errorCode, HttpStatus status) {
        super(message);

        this.errorCode = errorCode;
        this.status = status;
    }

    // Errors
    public static TodoException todoNotFound() {
        return new TodoException("Todo does not exist", "TODO_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    // put other errors here
}