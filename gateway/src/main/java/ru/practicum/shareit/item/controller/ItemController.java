package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с items
 * Реализует CRUD-операции для предметов, поиск и управление комментариями
 */
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Создание нового предмета
     *
     * @param userId ID пользователя-владельца (из заголовка X-Sharer-User-Id)
     * @param requestDto DTO с данными для создания предмета
     * @return ResponseEntity с созданным предметом
     */
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid ItemCreateDto requestDto) {
        return itemClient.addItem(userId, requestDto);
    }

    /**
     * Обновление существующего предмета
     * Частичное обновление (только указанные поля)
     *
     * @param userId ID пользователя-владельца
     * @param itemId ID обновляемого предмета
     * @param requestDto DTO с данными для обновления
     * @return ResponseEntity с обновленным предметом
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid ItemUpdateDto requestDto) {
        return itemClient.updateItem(userId, itemId, requestDto);
    }

    /**
     * Удаление предмета
     *
     * @param userId ID пользователя-владельца
     * @param itemId ID удаляемого предмета
     * @return ResponseEntity с результатом операции
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    /**
     * Получение предмета по ID
     *
     * @param userId ID пользователя, запрашивающего предмет
     * @param itemId ID запрашиваемого предмета
     * @return ResponseEntity с информацией о предмете
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    /**
     * Получение всех предметов владельца с пагинацией
     *
     * @param userId ID пользователя-владельца
     * @param from начальная позиция пагинации (не может быть отрицательной)
     * @param size количество элементов на странице (минимум 1)
     * @return ResponseEntity со списком предметов владельца
     */
    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getItemsByOwner(userId, from, size);
    }

    /**
     * Поиск предметов по тексту в названии или описании
     * Доступно для бронирования предметы
     *
     * @param text текст для поиска (обязательный параметр)
     * @param from начальная позиция пагинации (не может быть отрицательной)
     * @param size количество элементов на странице (минимум 1)
     * @return ResponseEntity со списком найденных предметов
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                              @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.searchItems(text, from, size);
    }

    /**
     * Добавление комментария к предмету
     * Доступно только для пользователей, которые бронировали этот предмет
     *
     * @param userId ID пользователя, оставляющего комментарий
     * @param itemId ID предмета, к которому добавляется комментарий
     * @param requestDto DTO с данными комментария
     * @return ResponseEntity с созданным комментарием
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentCreateDto requestDto) {
        return itemClient.addComment(userId, itemId, requestDto);
    }
}
