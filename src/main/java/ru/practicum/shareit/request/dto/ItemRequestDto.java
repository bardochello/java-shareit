package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

// DTO для возврата запроса
public record ItemRequestDto(
        Long id,
        String description,
        LocalDateTime created,
        List<ItemRequestDto.ItemDto> items
) {
    public record ItemDto(
            Long id,
            String name,
            String description,
            Boolean available,
            Long requestId
    ) {
    }
}
