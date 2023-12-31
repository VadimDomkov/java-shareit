package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUser(long userId);

    void deleteUser(long userId);

    List<UserDto> getAllUsers();
}
