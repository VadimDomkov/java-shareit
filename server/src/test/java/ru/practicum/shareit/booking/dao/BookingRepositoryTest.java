package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item = new Item();
    private Booking booking;

    @BeforeEach
    void setUp() {

        user = User.builder().name("name").email("email@mail.com").build();

        userRepository.save(user);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        itemRepository.save(item);

        booking = Booking.builder()
                .start(LocalDateTime.of(2024, 1, 25, 2, 15))
                .end(LocalDateTime.of(2024, 12, 25, 2, 15))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();

        bookingRepository.save(booking);
    }

    @AfterEach
    private void deleteRequest() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).toList();

        assertTrue(list.size() == 1);
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(user.getId(),
                LocalDateTime.of(2024, 5, 25, 2, 15),
                LocalDateTime.of(2024, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(user.getId(),
                LocalDateTime.of(2023, 5, 25, 2, 15),
                LocalDateTime.of(2023, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.of(2025, 5, 25, 2, 15),
                LocalDateTime.of(2025, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(),
                LocalDateTime.of(2024, 5, 25, 2, 15),
                LocalDateTime.of(2024, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(user.getId(),
                LocalDateTime.of(2023, 5, 25, 2, 15),
                LocalDateTime.of(2023, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.of(2025, 5, 25, 2, 15),
                LocalDateTime.of(2025, 5, 25, 3, 15), pageable);

        assertTrue(list.size() == 1);
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> list = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, pageable);

        assertTrue(list.size() == 1);
    }
}