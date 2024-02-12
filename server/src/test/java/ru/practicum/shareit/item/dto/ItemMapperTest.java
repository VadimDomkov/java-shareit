package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;

    @Test
    void dtoToItem() {
        assertNull(itemMapper.dtoToItem(null));
    }

    @Test
    void itemToDto() {
        assertNull(itemMapper.itemToDto(null));
    }

    @Test
    void itemToDto_withRequest() {
        Item item = new Item();
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .build();
        item.setRequest(request);

        ItemDto result = itemMapper.itemToDto(item);

        assertEquals(item.getRequest().getId(), result.getRequestId());
    }

    @Test
    void itemToExtDto() {
        assertNull(itemMapper.itemToExtDto(null));
    }
}