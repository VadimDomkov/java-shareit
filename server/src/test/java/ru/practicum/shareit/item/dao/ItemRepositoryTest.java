package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User user;

    private User user2;

    private Item item = new Item();

    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
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

        item.setOwner(user);
        item.setDescription("description");
        item.setName("name");
        item.setAvailable(true);
        item.setRequest(itemRequest);

        itemRepository.save(item);
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByOwnerId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> itemList = itemRepository.findAllByOwnerId(user.getId(), pageable);

        assertTrue(itemList.size() == 1);
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void searchByText() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> itemList = itemRepository.searchByText("description", pageable).toList();

        assertTrue(itemList.size() == 1);
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void findAllByRequestId() {
        List<Item> itemList = itemRepository.findAllByRequestId(itemRequest.getId());

        assertTrue(itemList.size() == 1);
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }
}