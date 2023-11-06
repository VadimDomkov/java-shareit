package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto updateItem(Long itemId, ItemDto item, Long userId);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItemsByName(String text, Long userId);
}
