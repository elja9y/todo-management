package com.github.elja9y.todo.service;

import com.github.elja9y.todo.dto.auth.LoginRequest;
import com.github.elja9y.todo.dto.auth.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(LoginRequest request);
}
