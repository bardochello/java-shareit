package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// Хранилище вещей в памяти
@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Создание новой вещи
    @Override
    public Item addItem(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }
        items.put(item.getId(), item);
        return item;
    }

    // Обновление вещи
    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    // Получение вещи по ID
    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    // Удаление вещи
    @Override
    public void deleteItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        items.remove(itemId);
    }

    // Получение всех вещей владельца
    @Override
    public List<Item> getItemsByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    // Поиск вещей
    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String searchText = text.toLowerCase().trim();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }
}
