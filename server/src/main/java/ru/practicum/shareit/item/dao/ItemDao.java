package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item);

    Item updateItem(long itemId, Item item);

    Item returnItem(long itemId);

    List<Item> returnUserItems(long userId);

    List<Item> searchItems(String text);
}
