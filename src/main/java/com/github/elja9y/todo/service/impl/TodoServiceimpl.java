package com.github.elja9y.todo.service.impl;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.dto.UpdateTodoRequest;
import com.github.elja9y.todo.entity.Todo;
import com.github.elja9y.todo.exception.TodoException;
import com.github.elja9y.todo.mapper.TodoStructMapper;
import com.github.elja9y.todo.repository.TodoRepository;
import com.github.elja9y.todo.service.TodoService;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceimpl implements TodoService {
    private TodoRepository todoRepository;
//    private ModelMapper modelMapper;
    TodoStructMapper todoMapper;

    public TodoServiceimpl(TodoRepository todoRepository,
                           //ModelMapper modelMapper,
                           TodoStructMapper todoMapper){
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoResponse addTodo(CreateTodoRequest request) {
        Todo todo = todoMapper.toTodo(request);
//        Todo todo = modelMapper.map(request, Todo.class);

        Todo savedTodo = todoRepository.save(todo);

//        return TodoMapper.toTodoResponse(savedTodo);
        return todoMapper.toTodoResponse(savedTodo);
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = getTodoEntityById(id);

        return todoMapper.toTodoResponse(todo);
    }

    @Override
    public List<TodoResponse> getAllTodos(){
        List<Todo> todos = todoRepository.findAll();

        List<TodoResponse> response = todos
                .stream()
                .map(t -> todoMapper.toTodoResponse(t))
                .toList();

        return response;
    }

    @Override
    public TodoResponse updateTodo(Long id, UpdateTodoRequest request) {
        Todo todo = getTodoEntityById(id);

        todoMapper.updateTodo(request, todo);

        Todo savedTodo = todoRepository.save(todo);

        return todoMapper.toTodoResponse(todo);
    }

    @Override
    public void deleteTodoById(Long id) {
        Todo todo = getTodoEntityById(id);
        todoRepository.delete(todo);
    }

    @Override
    public TodoResponse toggleCompletedStatus(Long id) {
        Todo todo = getTodoEntityById(id);

        todo.setCompleted(!todo.isCompleted());

        Todo savedTodo = todoRepository.save(todo);

        return todoMapper.toTodoResponse(savedTodo);
    }

    // helper used in multiple places
    private Todo getTodoEntityById(Long id) {
        return todoRepository
                .findById(id)
                .orElseThrow(() -> TodoException.todoNotFound());
    }
}
