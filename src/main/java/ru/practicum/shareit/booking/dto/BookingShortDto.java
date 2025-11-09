package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

// Короткий DTO для бронирования (для last/next в Item)
public record BookingShortDto(
        Long id,
        Long bookerId,
        LocalDateTime start,
        LocalDateTime end
) {
}
