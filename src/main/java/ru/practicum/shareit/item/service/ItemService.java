package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, Long userId);

    ItemDto updateItem(Long itemId, ItemDto item, Long userId);

    ItemExtendedDto getItemById(long itemId, Long userId);

    List<ItemExtendedDto> getUserItems(Long userId, int from, int size);

    List<ItemDto> searchItemsByName(String text, Long userId, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
