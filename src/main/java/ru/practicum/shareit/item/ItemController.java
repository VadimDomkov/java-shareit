package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserIsNotPresentedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new UserIsNotPresentedException("Не указан пользователь");
        }
        item.setOwner(userId);
        ItemDto dto = itemService.createItem(item);
        return dto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new UserIsNotPresentedException("Не указан пользователь");
        }
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new UserIsNotPresentedException("Не указан пользователь");
        }
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new UserIsNotPresentedException("Не указан пользователь");
        }
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null) {
            throw new UserIsNotPresentedException("Не указан пользователь");
        }
        return itemService.searchItemsByName(text, userId);
    }
}
