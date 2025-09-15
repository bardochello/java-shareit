package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// DTO для создания пользователя
public record UserCreateDto(
        // Имя пользователя, не может быть пустым или null
        @NotEmpty(message = "Имя пользователя не может быть пустым") String name,
        // Email пользователя, должен быть валидным и не null
        @NotNull(message = "Email не может быть null") @Email(message = "Email должен быть валидным") String email
) {
}
