package com.github.elja9y.todo.dto.auth;

import jdk.jfr.Name;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse{
    String accessToken;
    String tokenType = "Bearer";
}