package dev.m1stwng.taskflow.user.mapper;

import dev.m1stwng.taskflow.user.dto.response.UserResponse;
import dev.m1stwng.taskflow.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
