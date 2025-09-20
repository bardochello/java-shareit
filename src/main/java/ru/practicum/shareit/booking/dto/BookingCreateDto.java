package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO для создания бронирования
public record BookingCreateDto(
        @NotNull(message = "Дата начала не может быть null")
        LocalDateTime start,

        @NotNull(message = "Дата окончания не может быть null")
        LocalDateTime end,

        @NotNull(message = "ID вещи не может быть null")
        Long itemId
) {
}
