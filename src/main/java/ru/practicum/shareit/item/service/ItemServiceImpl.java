package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        //Проверка, что пользователь существует
        userService.getUser(userId);
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwner(userId);
        return itemMapper.itemToDto(itemDao.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) {
        if (!checkIfItemExists(itemId)) {
            throw new ItemNotFoundException(String.format("Предмета с id %d не найдено", itemId));
        }

        Item targetItem = itemDao.returnItem(itemId);
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
        return itemMapper.itemToDto(itemDao.updateItem(itemId, targetItem));
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        if (!checkIfItemExists(itemId)) {
            throw new ItemNotFoundException(String.format("Предмета с id %d не найдено", itemId));
        }
        return itemMapper.itemToDto(itemDao.returnItem(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userService.getUser(userId);
        return itemDao.returnUserItems(userId).stream()
                .map(item -> itemMapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text, Long userId) {
        return itemDao.searchItems(text).stream()
                .map(item -> itemMapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    private boolean checkIfItemExists(Long itemId) {
        if (itemDao.returnItem(itemId) == null) {
            return false;
        }
        return true;
    }
}
