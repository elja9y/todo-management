package com.github.elja9y.todo.mapper;

import com.github.elja9y.todo.dto.auth.RegisterRequest;
import com.github.elja9y.todo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserStructMapper {
    User toUser(RegisterRequest request);
}
