package com.github.elja9y.todo.dto.todo;

public record TodoResponse(Long id, String title, String description, boolean completed) {}
