package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortDto;  // Added import
import java.util.List;  // Added import (instead of java.util.List)

// DTO для вещи с бронированиями и комментариями
public record ItemWithBookingsDto(
        Long id,
        String name,
        String description,
        Boolean available,
        BookingShortDto lastBooking,  // Now short name
        BookingShortDto nextBooking,  // Now short name
        List<CommentDto> comments  // Now short name with import
) {
}
