package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
        @NotBlank(message = "Текст комментария не может быть пустым")
        String text
) {}
