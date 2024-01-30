package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exceptions.EmailBelongsToOtherUserException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@ContextConfiguration(classes = {UserServiceImpl.class, UserDtoMapperImpl.class})
class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @SpyBean
    UserDtoMapper userDtoMapper;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        user = User.builder()
                .name("name")
                .email("email")
                .build();
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto.getName(), result.getName());

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        Mockito.verify(userDtoMapper, Mockito.times(1)).userToDto(any(User.class));
        Mockito.verify(userDtoMapper, Mockito.times(1)).dtoToUser(any(UserDto.class));
    }

    @Test
    void createUser_whenEmailAlreadyExists_thenThrowException() {
        Mockito.when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        Exception exception = assertThrows(
                EmailBelongsToOtherUserException.class,
                () -> {
                    userService.createUser(userDto);
                }
        );

        assertEquals("Email email already in use", exception.getMessage());
    }

    @Test
    void updateUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(List.of());
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1, userDto);

        assertEquals(user.getName(), result.getName());
    }

    @Test
    void updateUser_whenEmailInUse_thenThrow() {
        User user2 = User.builder()
                .name("name")
                .email("newMail")
                .build();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user2));
        userDto.setEmail("newMail");
        Exception exception = assertThrows(
                EmailBelongsToOtherUserException.class,
                () -> {
                    userService.updateUser(1L, userDto);
                }
        );

        assertEquals("Email newMail already in use", exception.getMessage());
    }

    @Test
    void getUser_whenUserNotExists_thenThrowException() {
        Mockito.when(userRepository.findById(1L)).thenThrow(new EntityNotFoundException("Пользователя с id 1 не найдено"));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    userService.getUser(1);
                }
        );

        assertEquals("Пользователя с id 1 не найдено", exception.getMessage());
    }

    @Test
    void getUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1);

        assertEquals(userDto.getName(), result.getName());
    }

    @Test
    void deleteUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void deleteUser_whenUserNotExists_thenThrowException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(null);

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    userService.deleteUser(1);
                }
        );

        assertEquals("Пользователя с id 1 не найдено", exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).deleteById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getName(), result.get(0).getName());
    }
}