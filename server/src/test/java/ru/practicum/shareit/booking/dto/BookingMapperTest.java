package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;

    private Booking booking;
    private BookingForItemDto bookingForItemDto;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        user = User.builder().id(1L).build();
        booking = Booking.builder()
                .item(item)
                .booker(user)
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2024, 5, 25, 2, 15))
                .end(LocalDateTime.of(2024, 6, 25, 2, 15))
                .build();
    }

    @Test
    void bookingToDto() {
        BookingDto result = bookingMapper.bookingToDto(booking);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
    }

    @Test
    void bookingToDto_null() {
        assertNull(bookingMapper.bookingToDto(null));
    }

    @Test
    void bookingToBookingItemDto() {
        BookingForItemDto result = bookingMapper.bookingToBookingItemDto(booking);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
    }

    @Test
    void bookingToBookingItemDto_userIsNull() {
        booking.setBooker(null);

        BookingForItemDto result = bookingMapper.bookingToBookingItemDto(booking);

        assertEquals(booking.getId(), result.getId());
        assertNull(result.getBookerId());
    }

    @Test
    void bookingToBookingItemDto_null() {
        assertNull(bookingMapper.bookingToBookingItemDto(null));
    }
}