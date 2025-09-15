package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserDto;

// DTO для возврата данных вещи
public record ItemDto (
        // Уникальный идентификатор вещи
        Long id,

        // Название вещи
        String name,

        // Описание вещи
        String description,

        // Статус доступности вещи
        Boolean available,

        // Владелец вещи
        UserDto owner,

        // ID запроса, если вещь создана по запросу
        Long requestId
) {
}
