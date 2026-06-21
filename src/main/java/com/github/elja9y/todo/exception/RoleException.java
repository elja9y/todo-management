package com.github.elja9y.todo.exception;

import org.springframework.http.HttpStatus;

public class RoleException extends AppException {
    public RoleException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    // Errors
    public static RoleException roleNotFound(){
        return new RoleException("Role not found","ROLE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    // put other errors here
}