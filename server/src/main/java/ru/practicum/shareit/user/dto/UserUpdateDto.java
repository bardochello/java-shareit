package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

// DTO для обновления пользователя
public record UserUpdateDto(
        // Имя пользователя, может быть null
        String name,

        // Email пользователя, должен быть валидным, если указан
        @Email(message = "Email должен быть валидным")
        String email
) {
}
