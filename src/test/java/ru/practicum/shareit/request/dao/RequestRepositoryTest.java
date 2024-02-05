package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private User user2;

    private ItemRequest itemRequest;

    @BeforeEach
    private void addRequest() {
        user = User.builder()
                .name("V")
                .email("vasya@hh.ru")
                .build();

        user2 = User.builder()
                .name("P")
                .email("petya@hh.ru")
                .build();

        userRepository.save(user);

        itemRequest = ItemRequest
                .builder()
                .requestor(user)
                .created(LocalDateTime.now())
                .description("desc")
                .build();

        requestRepository
                .save(itemRequest);
    }

    @Test
    void findById() {
        Optional<ItemRequest> request = requestRepository.findById(itemRequest.getId());

        assertTrue(request.isPresent());
        assertEquals(itemRequest.getDescription(), request.get().getDescription());
        assertEquals(itemRequest.getRequestor(), request.get().getRequestor());
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> list = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId());
        assertTrue(list.size() == 1);
    }

    @Test
    void findAllByRequestorIdNot_whenNotOwner_thenReturnCollection() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        List<ItemRequest> list = requestRepository.findAllByRequestorIdNot(user2.getId(), pageable).toList();
        assertTrue(list.size() == 1);
    }

    @Test
    void findAllByRequestorIdNot_whenOwner_thenReturnEmptyCollection() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        List<ItemRequest> list = requestRepository.findAllByRequestorIdNot(user.getId(), pageable).toList();
        assertTrue(list.size() == 0);
    }

    @AfterEach
    private void deleteRequest() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}