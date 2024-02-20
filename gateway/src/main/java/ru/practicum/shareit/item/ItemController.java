package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос POST к /items");
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос PATCH к /items/%d", itemId));
        return itemClient.updateItem(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Запрос GET к /items/%d", itemId));
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Запрос GET к /items");
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByName(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Запрос GET к /items/search");
        return itemClient.searchItemsByName(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info(String.format("Запрос POST к /items/%d/comment", itemId));
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
