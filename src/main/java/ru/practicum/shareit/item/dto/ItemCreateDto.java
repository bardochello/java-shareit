package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// DTO для создания вещи
public record ItemCreateDto(
        @NotEmpty(message = "Название вещи не может быть пустым") String name,
        @NotEmpty(message = "Описание вещи не может быть пустым") String description,
        @NotNull(message = "Статус доступности не может быть null") Boolean available,
        Long requestId
) {
}
