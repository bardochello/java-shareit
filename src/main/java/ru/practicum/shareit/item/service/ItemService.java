package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemCreateDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto);

    void deleteItem(Long userId, Long itemId);

    ItemWithBookingsDto getItemWithBookingsById(Long userId, Long itemId);

    List<ItemWithBookingsDto> getItemsWithBookingsByOwner(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto);
}
