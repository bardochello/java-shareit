package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    // Внедрение зависимостей
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Добавление вещи
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemCreateDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    // Обновление вещи
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody ItemUpdateDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    // Удаление вещи
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    // Получение вещи по ID (с бронированиями и комментариями)
    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getItemWithBookingsById(userId, itemId);
    }

    // Получение всех вещей владельца (с бронированиями)
    @GetMapping
    public List<ItemWithBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemsWithBookingsByOwner(userId, from, size);
    }

    // Поиск вещей
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return itemService.searchItems(text, from, size);
    }

    // Добавление комментария
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentCreateDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
