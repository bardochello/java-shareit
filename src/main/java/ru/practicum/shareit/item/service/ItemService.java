package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemCreateDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto);

    void deleteItem(Long userId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
}
