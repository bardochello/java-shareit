package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCreateDto(
        @NotBlank(message = "Название не может быть пустым")
        String name,
        @NotBlank(message = "Описание не может быть пустым")
        String description,
        @NotNull(message = "Доступность не может быть null")
        Boolean available,
        Long requestId // Опционально, как в ТЗ
) {}
