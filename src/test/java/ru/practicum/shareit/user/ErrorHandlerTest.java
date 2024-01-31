package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.*;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    private MockMvc mockMvc;

    @Mock
    private UserController userController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void EmailBelongsToOtherUserException() throws Exception {
        Mockito.when(userController
                .getUser(anyLong()))
                .thenThrow(new EmailBelongsToOtherUserException("email is in use"));

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.error").value("email is in use"));
    }

    @Test
    void EntityNotFoundException() throws Exception {
        Mockito.when(userController
                .getUser(anyLong()))
                .thenThrow(new EntityNotFoundException("user not found"));

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error").value("user not found"));
    }

    @Test
    void IncorrectParamException() throws Exception {
        Mockito.when(userController
                .getUser(anyLong()))
                .thenThrow(new IncorrectParamException("incorrect param"));

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("incorrect param"));
    }

    @Test
    void ValueIsNotEnumException() throws Exception {
        Mockito.when(userController
                .getUser(anyLong()))
                .thenThrow(new ValueIsNotEnumException("No such constant"));

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("No such constant"));
    }

    @Test
    void RuntimeException() throws Exception {
        Mockito.when(userController
                .getUser(anyLong()))
                .thenThrow(new RuntimeException("Unexpected exception"));

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("Unexpected exception"));
    }
}