package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Item item);

    ItemDto updateItem(Long itemId, Item item, Long userId);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItemsByName(String text, Long userId);
}
