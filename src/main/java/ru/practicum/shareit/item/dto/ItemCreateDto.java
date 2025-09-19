package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// DTO для создания вещи
public record ItemCreateDto(
        // Название вещи, не может быть пустым или null
        @NotEmpty(message = "Название вещи не может быть пустым") String name,
        // Описание вещи, не может быть пустым или null
        @NotEmpty(message = "Описание вещи не может быть пустым") String description,
        // Статус доступности вещи, не может быть null
        @NotNull(message = "Статус доступности не может быть null") Boolean available,
        // ID запроса, если вещь создаётся по запросу
        Long requestId
) {
}
