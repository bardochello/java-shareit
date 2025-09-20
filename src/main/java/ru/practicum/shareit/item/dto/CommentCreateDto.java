package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

// DTO для создания комментария
public record CommentCreateDto(
        @NotBlank(message = "Текст комментария не может быть пустым")
        String text
) {
}
