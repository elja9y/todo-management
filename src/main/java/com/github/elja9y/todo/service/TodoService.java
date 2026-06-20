package com.github.elja9y.todo.service;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;

public interface TodoService {
    TodoResponse addTodo(CreateTodoRequest request);
}