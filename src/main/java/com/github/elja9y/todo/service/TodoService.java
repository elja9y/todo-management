package com.github.elja9y.todo.service;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    TodoResponse addTodo(CreateTodoRequest request);
    TodoResponse getTodoById(Long id);
    List<TodoResponse> getAllTodos();
}