package com.github.elja9y.todo.exception;

import org.springframework.http.HttpStatus;

public class UserException extends AppException {
    public UserException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    // Errors
    public static UserException userNotFound() {
        return new UserException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public static UserException duplicatedUsername() {
        return new UserException("This username is not available", "DUPLICATED_USERNAME", HttpStatus.BAD_REQUEST);
    }

    public static UserException duplicatedEmail() {
        return new UserException("This email is already registered", "DUPLICATED_EMAIL", HttpStatus.BAD_REQUEST);
    }

    // put other errors here
}