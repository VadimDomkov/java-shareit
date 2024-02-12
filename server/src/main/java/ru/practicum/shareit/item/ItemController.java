package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос POST к /items");
        ItemDto createdItem = itemService.createItem(item, userId);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос PATCH к /items/%d", itemId));
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос GET к /items/%d", itemId));
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemExtendedDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Запрос GET к /items");
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Запрос GET к /items/search");
        return itemService.searchItemsByName(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info(String.format("Запрос POST к /items/%d/comment", itemId));
        return itemService.addComment(userId, itemId, commentDto);
    }

}
