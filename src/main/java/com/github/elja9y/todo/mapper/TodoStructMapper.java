package com.github.elja9y.todo.mapper;

import com.github.elja9y.todo.dto.CreateTodoRequest;
import com.github.elja9y.todo.dto.TodoResponse;
import com.github.elja9y.todo.entity.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoStructMapper {
    Todo toTodo(CreateTodoRequest request);
    TodoResponse toTodoResponse(Todo todo);
}
