package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.IncorrectParamException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос POST к /items");
        if (userId == null) {
            throw new IncorrectParamException("Не указан пользователь");
        }
        ItemDto createdItem = itemService.createItem(item, userId);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос PATCH к /items/%d", itemId));
        if (userId == null) {
            throw new IncorrectParamException("Не указан пользователь");
        }
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос GET к /items/%d", itemId));
        if (userId == null) {
            throw new IncorrectParamException("Не указан пользователь");
        }
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemExtendedDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос GET к /items");
        if (userId == null) {
            throw new IncorrectParamException("Не указан пользователь");
        }
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос GET к /items/search");
        if (userId == null) {
            throw new IncorrectParamException("Не указан пользователь");
        }
        return itemService.searchItemsByName(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.info(String.format("Запрос POST к /items/%d/comment", itemId));
        return itemService.addComment(userId, itemId, commentDto);
    }

}
