package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailBelongsToOtherUserException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserDtoMapper userDtoMapper;

    @Override
    public UserDto createUser(User user) {
        if (checkEmail(user)) {
            throw new EmailBelongsToOtherUserException(String.format("Email %s already in use", user.getEmail()));
        }
        return userDtoMapper.userToDto(userDao.addUser(user));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (!checkIfUserExists(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId));
        }
        User userTarget = userDao.getUser(userId);
        if (userDto.getName() != null) {
            userTarget.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (checkEmail(userDtoMapper.dtoToUser(userDto)) && !userDto.getEmail().equals(userTarget.getEmail())) {
                throw new EmailBelongsToOtherUserException(String.format("Email %s already in use", userDto.getEmail()));
            }
            userTarget.setEmail(userDto.getEmail());
        }
        return userDtoMapper.userToDto(userDao.updateUser(userId, userTarget));
    }

    @Override
    public UserDto getUser(long userId) {
        if (!checkIfUserExists(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId));
        }
        return userDtoMapper.userToDto(userDao.getUser(userId));
    }

    @Override
    public void deleteUser(long userId) {
        if (!checkIfUserExists(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId));
        }
        userDao.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getUsers().stream()
                .map(user -> userDtoMapper.userToDto(user))
                .collect(Collectors.toList());
    }

    private boolean checkEmail(User user) {
        return userDao.getUsers().stream().filter(user1 -> user1.getEmail().equals(user.getEmail())).collect(Collectors.toList()).size() > 0;
    }

    private boolean checkIfUserExists(Long userId) {
        if (userDao.getUser(userId) == null) {
            return false;
        }
        return true;
    }
}
