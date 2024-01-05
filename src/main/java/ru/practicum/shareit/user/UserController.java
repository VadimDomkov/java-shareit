package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Запрос POST к /users");
        return userService.createUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto user) {
        log.info(String.format("Запрос PATCH к /users/%d", userId));
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info(String.format("Запрос GET к /users/%d", userId));
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info(String.format("Запрос DELETE к /users/%d", userId));
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос GET к /users");
        return userService.getAllUsers();
    }
}
