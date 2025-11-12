package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с user
 * Реализует полный набор CRUD-операций для управления пользователями системы
 */
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    /**
     * Создание нового пользователя
     *
     * @param requestDto DTO с данными для создания пользователя (имя, email)
     * @return ResponseEntity с созданным пользователем
     */
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreateDto requestDto) {
        return userClient.createUser(requestDto);
    }

    /**
     * Обновление данных существующего пользователя
     * Частичное обновление - только указанные поля
     *
     * @param userId ID обновляемого пользователя
     * @param requestDto DTO с данными для обновления
     * @return ResponseEntity с обновленными данными пользователя
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Valid UserUpdateDto requestDto) {
        return userClient.updateUser(userId, requestDto);
    }

    /**
     * Удаление пользователя по ID
     *
     * @param userId ID удаляемого пользователя
     * @return ResponseEntity с результатом операции (без тела)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return userClient.deleteUser(userId);
    }

    /**
     * Получение пользователя по ID
     *
     * @param userId ID запрашиваемого пользователя
     * @return ResponseEntity с данными пользователя
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userClient.getUserById(userId);
    }

    /**
     * Получение списка всех пользователей системы
     *
     * @return ResponseEntity со списком всех пользователей
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }
}
