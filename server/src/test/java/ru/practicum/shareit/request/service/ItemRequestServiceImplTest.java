package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private ItemRequestDto dto = new ItemRequestDto();
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;

    private User user2;

    private User savedUser;

    private User savedUser2;

    private Long requestId;

    @BeforeEach
    private void setUp() {
        user = User.builder()
                .name("V")
                .email("vasya@hh.ru")
                .build();
        user2 = User.builder()
                .name("P")
                .email("petya@hh.ru")
                .build();

        savedUser = userRepository.saveAndFlush(user);
        savedUser2 = userRepository.saveAndFlush(user2);

        requestId = requestRepository
                .save(ItemRequest
                        .builder()
                        .requestor(user)
                        .created(LocalDateTime.now())
                        .description("desc")
                        .build()).getId();
    }


    @Test
    void createRequest_whenUserIsPresented_thenCreateRequest() {
        dto.setDescription("desc");
        ItemRequestDto result = itemRequestService.createRequest(dto, 1L);

        assertEquals(dto.getDescription(), result.getDescription());
    }

    @Test
    void createRequest_whenUserIdIsInvalid_thenThrowException() {
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemRequestService.createRequest(dto, 10L);
                }
        );

        assertEquals("Пользователь с id 10 не найден", exception.getMessage());
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDto> list = itemRequestService.getUserRequests(user.getId());

        assertEquals(1, list.size());
    }

    @Test
    void getUserRequests_whenUserIdIsInvalid_thenThrowException() {
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemRequestService.getUserRequests(10L);
                }
        );

        assertEquals("Пользователь с id 10 не найден", exception.getMessage());
    }

    @Test
    void getAllRequests_whenUserIsNotOwner_thenReturnList() {
        List<ItemRequestDto> list = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertEquals(1, list.size());
    }

    @Test
    void getAllRequests_whenUserIsOwner_thenReturnEmpty() {
        List<ItemRequestDto> list = itemRequestService.getAllRequests(user.getId(), 0, 10);

        assertEquals(0, list.size());
    }

    @Test
    void getAllRequests_whenUserIdIsInvalid_thenThrowException() {
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemRequestService.getAllRequests(10L, 0, 1);
                }
        );

        assertEquals("Пользователь с id 10 не найден", exception.getMessage());
    }

    @Test
    void getRequestById_whenRequestIsValid_thenReturnRequest() {
        Long userId = savedUser.getId();
        ItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        assertEquals("desc", result.getDescription());
    }

    @AfterEach
    private void deleteRequest() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}