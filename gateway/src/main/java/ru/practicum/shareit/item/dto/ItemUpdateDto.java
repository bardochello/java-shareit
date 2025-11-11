package ru.practicum.shareit.item.dto;

public record ItemUpdateDto(
        String name, // Нет строгой валидации, так как partial update
        String description,
        Boolean available
) {}
