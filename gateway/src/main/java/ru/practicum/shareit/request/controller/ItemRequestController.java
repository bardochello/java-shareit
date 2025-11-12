package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с запросами с item requests
 * Позволяет пользователям создавать запросы на предметы и просматривать существующие запросы
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    /**
     * Создание нового запроса на предмет
     * Пользователь описывает, какой предмет ему нужен, другие пользователи могут предложить свои предметы
     *
     * @param userId ID пользователя, создающего запрос (из заголовка X-Sharer-User-Id)
     * @param requestDto DTO с данными для создания запроса (содержит описание нужного предмета)
     * @return ResponseEntity с созданным запросом
     */
    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemRequestCreateDto requestDto) {
        return itemRequestClient.addRequest(userId, requestDto);
    }

    /**
     * Получение всех запросов текущего пользователя
     * Возвращает запросы, созданные данным пользователем, вместе с предложенными предметами
     *
     * @param userId ID пользователя, чьи запросы запрашиваются
     * @return ResponseEntity со списком запросов пользователя
     */
    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getRequestsByUser(userId);
    }

    /**
     * Получение всех запросов других пользователей с пагинацией
     * Используется для просмотра запросов, на которые пользователь может предложить свои предметы
     * Не включает запросы текущего пользователя
     *
     * @param userId ID пользователя, запрашивающего список
     * @param from начальная позиция пагинации (не может быть отрицательной)
     * @param size количество элементов на странице (минимум 1)
     * @return ResponseEntity со списком запросов других пользователей
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    /**
     * Получение конкретного запроса по ID
     * Возвращает информацию о запросе вместе с предложенными предметами
     *
     * @param userId ID пользователя, запрашивающего информацию
     * @param requestId ID запрашиваемого запроса
     * @return ResponseEntity с информацией о запросе
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
