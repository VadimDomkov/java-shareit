package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Formatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    Booking booking;
    BookingDto bookingDto;
    BookingRequestDto bookingRequestDto;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .end(LocalDateTime.of(2024, 6, 25, 2, 15))
                .start(LocalDateTime.of(2024, 5, 25, 2, 15))
                .build();

        user = User.builder().id(1L).build();
        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        booking = Booking.builder().booker(user).item(item).build();

        bookingDto = BookingDto.builder()
                .end(LocalDateTime.of(2024, 6, 25, 2, 15))
                .start(LocalDateTime.of(2024, 5, 25, 2, 15))
                .item(item)
                .build();

        bookingDto.setBooker(user);
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.of(2024, 5, 25, 2, 15));
        bookingRequestDto.setEnd(LocalDateTime.of(2024, 6, 25, 2, 15));

    }

    @Test
    @SneakyThrows
    void createBooking() {
        Mockito.when(bookingService.createBooking(any(BookingRequestDto.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())));
    }

    @Test
    @SneakyThrows
    void approveBooking() {
        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SneakyThrows
    void getBookingById() {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())));
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUserId() {
        Mockito.when(bookingService.getAllBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @SneakyThrows
    void getAllBookingsForUserItems() {
        Mockito.when(bookingService.getAllBookingsForUserItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/bookings/owner")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}