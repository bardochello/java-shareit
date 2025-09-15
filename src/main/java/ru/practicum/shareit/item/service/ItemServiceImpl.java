package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto addItem(Long userId, ItemCreateDto itemDto) {
        userService.getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userService.getUserOrThrow(userId));
        return itemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto) {
        Item item = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена у пользователя");
        }
        String originalName = item.getName();
        String originalDescription = item.getDescription();
        Boolean originalAvailable = item.getAvailable();
        itemMapper.updateItem(itemDto, item);
        if (item.getName() == null) item.setName(originalName);
        if (item.getDescription() == null) item.setDescription(originalDescription);
        if (item.getAvailable() == null) item.setAvailable(originalAvailable);
        return itemMapper.toItemDto(itemStorage.updateItem(item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не принадлежит пользователю с id=" + userId);
        }
        itemStorage.deleteItem(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        userService.getUserOrThrow(userId);
        return itemStorage.getItemsByOwner(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
