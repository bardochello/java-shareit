package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * Контроллер для управления бронированиями.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    // Внедрение зависимостей
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Добавление нового бронирования
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingCreateDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    // Подтверждение или отклонение бронирования
    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    // Получение бронирования по ID
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    // Получение всех бронирований пользователя
    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsByUser(userId, state, from, size);
    }

    // Получение всех бронирований владельца
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsByOwner(userId, state, from, size);
    }
}
