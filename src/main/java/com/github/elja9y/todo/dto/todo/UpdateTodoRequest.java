package com.github.elja9y.todo.dto.todo;

public record UpdateTodoRequest(
        String title,
        String description
        //boolean completed
) {}
