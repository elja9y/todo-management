package com.github.elja9y.todo.dto.auth;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}
