package com.github.elja9y.todo.controller;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.dto.UpdateTodoRequest;
import com.github.elja9y.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/todos")
public class TodoController {
    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoResponse> addTodo(@RequestBody CreateTodoRequest request) {
        return new ResponseEntity<TodoResponse>(todoService.addTodo(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @GetMapping()
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @PutMapping("{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id,
                                                   @RequestBody UpdateTodoRequest request){
        TodoResponse response = todoService.updateTodo(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id){
        todoService.deleteTodoById(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}