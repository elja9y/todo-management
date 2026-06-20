package com.github.elja9y.todo.controller;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/todos")
public class TodoController {
    private TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoResponse> addTodo(@RequestBody CreateTodoRequest request){
        return new ResponseEntity<TodoResponse>(todoService.addTodo(request), HttpStatus.CREATED);
    }
}
