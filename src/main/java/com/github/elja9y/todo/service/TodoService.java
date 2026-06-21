package com.github.elja9y.todo.service;

import com.github.elja9y.todo.dto.todo.CreateTodoRequest;
import com.github.elja9y.todo.dto.todo.TodoResponse;
import com.github.elja9y.todo.dto.todo.UpdateTodoRequest;

import java.util.List;

public interface TodoService {
    TodoResponse addTodo(CreateTodoRequest request);
    TodoResponse getTodoById(Long id);
    List<TodoResponse> getAllTodos();
    TodoResponse updateTodo(Long id, UpdateTodoRequest request);
    void deleteTodoById(Long id);
    TodoResponse toggleCompletedStatus(Long id);
}