package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        String name,
        @Email(message = "Email должен быть валидным") String email
) {}
