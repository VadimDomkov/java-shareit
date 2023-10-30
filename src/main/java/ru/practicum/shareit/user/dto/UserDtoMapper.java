package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    User dtoToUser(UserDto userDto);

    UserDto userToDto(User user);
}
