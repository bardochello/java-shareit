package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BookingClient;

/**
 * Контроллер для обработки HTTP-запросов, связанных с бронированием предметов
 * Взаимодействует с (BookingClient) для выполнения бизнес-логики
 */

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    /**
     * Создание нового бронирования
     *
     * @param userId ID пользователя, создающего бронирование (из заголовка)
     * @param requestDto DTO с данными для создания бронирования
     * @return ResponseEntity с результатом операции
     */
    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid BookingCreateDto requestDto) {
        return bookingClient.addBooking(userId, requestDto);
    }

    /**
     * Подтверждение или отклонение бронирования владельцем предмета
     *
     * @param userId ID пользователя (владельца предмета)
     * @param bookingId ID бронирования, которое нужно подтвердить/отклонить
     * @param approved флаг подтверждения (true - подтвердить, false - отклонить)
     * @return ResponseEntity с результатом операции
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    /**
     * Получение информации о конкретном бронировании по ID
     *
     * @param userId ID пользователя, запрашивающего информацию
     * @param bookingId ID запрашиваемого бронирования
     * @return ResponseEntity с информацией о бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    /**
     * Получение списка бронирований текущего пользователя
     *
     * @param userId ID пользователя, чьи бронирования запрашиваются
     * @param stateParam состояние бронирований для фильтрации (по умолчанию "ALL")
     * @param from начальная позиция пагинации (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return ResponseEntity со списком бронирований пользователя
     */
    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getBookingsByUser(userId, stateParam, from, size);
    }

    /**
     * Получение списка бронирований для всех предметов владельца
     *
     * @param userId ID владельца предметов
     * @param stateParam состояние бронирований для фильтрации (по умолчанию "ALL")
     * @param from начальная позиция пагинации (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return ResponseEntity со списком бронирований предметов владельца
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getBookingsByOwner(userId, stateParam, from, size);
    }
}
