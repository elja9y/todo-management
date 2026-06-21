package com.github.elja9y.todo.dto.auth;

public record RegisterRequest(
        String name,
        String username,
        String email,
        String password
) {}
