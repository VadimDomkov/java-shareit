package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoMemory implements UserDao {
    private long id = 1;
    private Map<Long, User> idToUser = new HashMap<>();

    @Override
    public User addUser(User user) {
        long userId = generateId();
        user.setId(userId);
        idToUser.put(userId, user);
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        idToUser.put(userId, user);
        return user;
    }

    @Override
    public User getUser(long userId) {
        return idToUser.get(userId);
    }

    @Override
    public void deleteUser(long userId) {
        idToUser.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(idToUser.values());
    }

    private long generateId() {
        return id++;
    }
}
