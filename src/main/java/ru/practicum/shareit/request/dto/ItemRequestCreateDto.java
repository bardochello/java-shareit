package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

// DTO для создания запроса
public record ItemRequestCreateDto(
        @NotBlank(message = "Описание не может быть пустым")
        String description
) {
}
