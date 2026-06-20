package com.github.elja9y.todo.service.impl;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.entity.Todo;
import com.github.elja9y.todo.mapper.TodoMapper;
import com.github.elja9y.todo.repository.TodoRepository;
import com.github.elja9y.todo.service.TodoService;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceimpl implements TodoService {
    private TodoRepository todoRepository;

    public TodoServiceimpl(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse addTodo(CreateTodoRequest request) {
        Todo todo = TodoMapper.toTodo(request);

        Todo savedTodo = todoRepository.save(todo);

        return TodoMapper.toTodoResponse(savedTodo);
    }
}
