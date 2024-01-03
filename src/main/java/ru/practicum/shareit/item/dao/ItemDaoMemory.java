package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDaoMemory implements ItemDao {
    private long id = 1;
    private Map<Long, Item> idToItem = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        long id = generateId();
        item.setId(id);
        idToItem.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(long itemId, Item item) {
        idToItem.put(itemId, item);
        return item;
    }

    @Override
    public Item returnItem(long itemId) {
        return idToItem.get(itemId);
    }

    @Override
    public List<Item> returnUserItems(long userId) {
        return idToItem.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return idToItem.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true)
                )
                .collect(Collectors.toList());
    }

    private long generateId() {
        return id++;
    }
}
