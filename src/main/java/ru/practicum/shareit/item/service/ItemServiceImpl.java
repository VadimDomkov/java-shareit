package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        //Проверка, что пользователь существует
        userService.getUser(userId);
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId))));
        return itemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) {

        Item targetItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмета с id %d не найдено", itemId)));
        if (!targetItem.getOwner().equals(userId)) {
            throw new ItemNotFoundException(String.format("Предмета с id %d у пользователя %d не найдено", itemId, userId));
        }
        if (item.getName() != null) {
            targetItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            targetItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            targetItem.setAvailable(item.getAvailable());
        }
        return itemMapper.itemToDto(itemRepository.save(targetItem));
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        return itemMapper.itemToDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмета с id %d не найдено", itemId))));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userService.getUser(userId);
        return itemRepository.findByOwner(userId).stream()
                .map(item -> itemMapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text, Long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByText(text).stream()
                .map(item -> itemMapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    private boolean checkIfItemExists(Long itemId) {
        if (itemRepository.findById(itemId) == null) {
            return false;
        }
        return true;
    }
}
