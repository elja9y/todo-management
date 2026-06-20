package com.github.elja9y.todo.mapper;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.entity.Todo;

public class TodoMapper {
    public static TodoResponse toTodoResponse(Todo todo){
        TodoResponse response = new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted()
        );

        return response;
    }

    public static Todo toTodo(CreateTodoRequest request){
        Todo todo = new Todo();

        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(false);

        return todo;
    }
}
