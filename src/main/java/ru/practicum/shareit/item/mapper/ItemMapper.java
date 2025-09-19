package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import org.mapstruct.*;

// Маппер для преобразования между сущностью вещи и DTO
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    // Конвертация ItemCreateDto в сущность Item
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item toItem(ItemCreateDto dto);

    // Конвертация сущности Item в ItemDto
    ItemDto toItemDto(Item item);

    // Обновление сущности Item из ItemUpdateDto
    void updateItem(ItemUpdateDto dto, @MappingTarget Item item);
}
