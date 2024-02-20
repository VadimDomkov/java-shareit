package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailBelongsToOtherUserException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userDtoMapper.dtoToUser(userDto);
        try {
            return userDtoMapper.userToDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new EmailBelongsToOtherUserException(String.format("Email %s already in use", user.getEmail()));
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User userTarget = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
        if (userDto.getName() != null) {
            userTarget.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (checkEmail(userDtoMapper.dtoToUser(userDto)) && !userDto.getEmail().equals(userTarget.getEmail())) {
                throw new EmailBelongsToOtherUserException(String.format("Email %s already in use", userDto.getEmail()));
            }
            userTarget.setEmail(userDto.getEmail());
        }
        return userDtoMapper.userToDto(userRepository.save(userTarget));
    }

    @Override
    public UserDto getUser(long userId) {
        return userDtoMapper.userToDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId))));
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (!checkIfUserExists(userId)) {
            throw new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId));
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> userDtoMapper.userToDto(user))
                .collect(Collectors.toList());
    }

    private boolean checkEmail(User user) {
        return userRepository.findAll().stream().filter(user1 -> user1.getEmail().equals(user.getEmail())).collect(Collectors.toList()).size() > 0;
    }

    private boolean checkIfUserExists(Long userId) {
        if (userRepository.findById(userId) == null) {
            return false;
        }
        return true;
    }
}
