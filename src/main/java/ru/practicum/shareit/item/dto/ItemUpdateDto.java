package ru.practicum.shareit.item.dto;

// DTO для обновления вещи
public record ItemUpdateDto(
        // Название вещи, может быть null
        String name,

        // Описание вещи, может быть null
        String description,

        // Статус доступности вещи, может быть null
        Boolean available
) {
}
