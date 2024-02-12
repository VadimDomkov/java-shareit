package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingForItemDtoTest {
    @Autowired
    private JacksonTester<BookingForItemDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerializations() {
        BookingForItemDto booking = BookingForItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2023, 8, 4, 0, 0))
                .end(LocalDateTime.of(2023, 8, 4, 0, 0))
                .build();

        booking.setStatus(BookingStatus.WAITING);

        JsonContent<BookingForItemDto> result = jacksonTester.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(booking.getBookerId().intValue());
    }
}