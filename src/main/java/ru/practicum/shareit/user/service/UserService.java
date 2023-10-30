package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUser(long userId);

    void deleteUser(long userId);

    List<UserDto> getAllUsers();
}
