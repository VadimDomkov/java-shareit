package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestExtendedDtoTest {
    @Autowired
    private JacksonTester<ItemRequestExtendedDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerializations() {
        ItemRequestExtendedDto dto = ItemRequestExtendedDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 8, 4, 0, 0))
                .items(List.of())
                .build();

        dto.setDescription("description");
        JsonContent<ItemRequestExtendedDto> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());

    }
}