package com.github.elja9y.todo.mapper;

import com.github.elja9y.todo.dto.todo.CreateTodoRequest;
import com.github.elja9y.todo.dto.todo.TodoResponse;
import com.github.elja9y.todo.dto.todo.UpdateTodoRequest;
import com.github.elja9y.todo.entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TodoStructMapper {
    Todo toTodo(CreateTodoRequest request);
    TodoResponse toTodoResponse(Todo todo);

    @Mapping(target = "completed", ignore = true)
    void updateTodo(UpdateTodoRequest request, @MappingTarget Todo todo);

    // @MappingTarget tells MapStruct to update the existing object instead of creating a new one, and ignore = true leaves completed untouched.
}

