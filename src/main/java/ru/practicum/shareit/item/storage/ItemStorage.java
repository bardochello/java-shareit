package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItemById(Long itemId);

    void deleteItem(Long itemId);

    List<Item> getItemsByOwner(Long userId);

    List<Item> searchItems(String text);
}
