package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserDto;

// DTO для возврата данных вещи (без бронирований и комментариев)
public record ItemDto(
        Long id,
        String name,
        String description,
        Boolean available,
        UserDto owner,
        Long requestId
) {
}
