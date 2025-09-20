package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO для создания бронирования
public record BookingCreateDto(
        @NotNull(message = "Дата начала не может быть null")
        @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
        LocalDateTime start,

        @NotNull(message = "Дата окончания не может быть null")
        @Future(message = "Дата окончания должна быть в будущем")
        LocalDateTime end,

        @NotNull(message = "ID вещи не может быть null")
        Long itemId
) {
}
