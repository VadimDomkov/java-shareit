package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    User updateUser(long userId, User user);

    User getUser(long userId);

    void deleteUser(long userId);

    List<User> getUsers();
}
