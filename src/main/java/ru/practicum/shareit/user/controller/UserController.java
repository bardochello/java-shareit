package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // Внедрение зависимостей
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создание нового пользователя
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserCreateDto userDto) {
        return userService.createUser(userDto);
    }

    // Обновление пользователя
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    // Удаление пользователя
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    // Получение пользователя по ID
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    // Получение всех пользователей
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
