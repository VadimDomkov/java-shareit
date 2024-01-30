package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParamException;
import ru.practicum.shareit.exceptions.ValueIsNotEnumException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@ContextConfiguration(classes = {BookingServiceImpl.class, BookingMapperImpl.class})
class BookingServiceImplTest {
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @SpyBean
    Booking booking;
    BookingDto bookingDto;
    BookingRequestDto bookingRequestDto;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder().build();

        user = User.builder().id(1L).build();
        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        booking = Booking.builder().booker(user).item(item).build();

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.of(2024, 5, 25, 2, 15));
        bookingRequestDto.setEnd(LocalDateTime.of(2024, 6, 25, 2, 15));
    }

    @Test
    void createBooking() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingRequestDto, 5L);

        assertEquals(bookingRequestDto.getItemId(), result.getItem().getId());
    }

    @Test
    void createBooking_whenUnavailable_thenThrow() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(
                IncorrectParamException.class,
                () -> {
                    bookingService.createBooking(bookingRequestDto, 5L);
                }
        );

        assertEquals("Предмет недоступен", exception.getMessage());
    }

    @Test
    void createBooking_whenOwnerIsBooker_thenThrow() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.createBooking(bookingRequestDto, 1L);
                }
        );

        assertEquals("Предмет с id 1 не доступен для бронирования пользователю 1", exception.getMessage());
    }

    @Test
    void createBooking_whenTimeIsIncorrect_thenThrow() {
        bookingRequestDto.setEnd(LocalDateTime.of(2024, 4, 25, 2, 15));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception exception = assertThrows(
                IncorrectParamException.class,
                () -> {
                    bookingService.createBooking(bookingRequestDto, 5L);
                }
        );

        assertEquals("Проверьте корректность дат", exception.getMessage());
    }

    @Test
    void approveBooking() {
        booking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.approveBooking(1L, 1L, true);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(any());
    }

    @Test
    void approveBookingReject() {
        booking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.approveBooking(1L, 1L, false);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(any());
    }

    @Test
    void approveBooking_whenUserNotOwner_thenThrow() {
        booking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.approveBooking(1L, 2L, true);
                }
        );

        assertEquals("Пользователь 2 не владелец вещи 1", exception.getMessage());

        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void approveBooking_whenStatusApproved_thenThrow() {
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(
                IncorrectParamException.class,
                () -> {
                    bookingService.approveBooking(1L, 1L, true);
                }
        );

        assertEquals("Запрос 1 уже подтвержден", exception.getMessage());

        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void getBookingById() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto dto = bookingService.getBookingById(1L, 1L);
        assertEquals(booking.getId(), dto.getId());
    }

    @Test
    void getBookingById_whenWrongUser_thenThrow() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    bookingService.getBookingById(1L, 2L);
                }
        );

        assertEquals("Запроса 1 для пользователя 2 не найдено", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserId_ALL() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_CURRENT() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "CURRENT", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_FUTURE() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "FUTURE", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_PAST() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "PAST", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_WAITING() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "WAITING", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_REJECTED() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsByUserId(1L, "REJECTED", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsByUserId_whenUNKNOWNstate_thenThrow() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                ValueIsNotEnumException.class,
                () -> {
                    bookingService.getAllBookingsByUserId(1L, "UNKNOWN", 0, 10);
                }
        );

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getAllBookingsForUserItems_ALL() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "ALL", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_CURRENT() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "CURRENT", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_FUTURE() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "FUTURE", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_PAST() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "PAST", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_WAITING() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "WAITING", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_REJECTED() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> dtoList = bookingService.getAllBookingsForUserItems(1L, "REJECTED", 0, 10);

        assertTrue(dtoList.size() == 1);
    }

    @Test
    void getAllBookingsForUserItems_whenUNKNOWNstate_thenThrow() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                ValueIsNotEnumException.class,
                () -> {
                    bookingService.getAllBookingsForUserItems(1L, "UNKNOWN", 0, 10);
                }
        );

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }
}