package ru.practicum.shareit.user.dto;

// DTO для возврата данных пользователя
public record UserDto(
        // Уникальный идентификатор пользователя
        Long id,

        // Имя пользователя
        String name,

        // Email пользователя
        String email
) {
}
