package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingCreateDto(
        @NotNull(message = "ID вещи не может быть null")
        Long itemId,
        @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
        LocalDateTime start,
        @Future(message = "Дата окончания должна быть в будущем")
        LocalDateTime end
) {
}
