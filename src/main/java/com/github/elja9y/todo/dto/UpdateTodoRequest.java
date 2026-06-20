package com.github.elja9y.todo.dto;

public record UpdateTodoRequest(
        String title,
        String description
        //boolean completed
) {}
