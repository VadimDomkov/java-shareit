package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RequestMapperTest {
    @Autowired
    private RequestMapper requestMapper;

    @Test
    void dtoToRequest() {
        assertNull(requestMapper.dtoToRequest(null));
    }

    @Test
    void itemToDto() {
        assertNull(requestMapper.itemToDto(null));
    }
}