package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserCreateDto(
        @NotEmpty(message = "Имя пользователя не может быть пустым") String name,
        @NotNull(message = "Email не может быть null") @Email(message = "Email должен быть валидным") String email
) {}
