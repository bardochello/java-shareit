package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import java.util.List;

// DTO для вещи с бронированиями и комментариями
public record ItemWithBookingsDto(
        Long id,
        String name,
        String description,
        Boolean available,
        BookingShortDto lastBooking,
        BookingShortDto nextBooking,
        List<CommentDto> comments
) {
}
