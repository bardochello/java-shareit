package ru.practicum.shareit.item.dto;

// DTO для обновления вещи
public record ItemUpdateDto(
        String name,
        String description,
        Boolean available
) {
}
