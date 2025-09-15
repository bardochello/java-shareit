package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Реализация сервиса для управления пользователями
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    // Конструктор для внедрения зависимостей
    public UserServiceImpl(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    // Создание нового пользователя
    @Override
    public UserDto createUser(UserCreateDto userDto) {
        // Проверка, что email не null
        if (userDto.email() == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }

        // Проверка уникальности email
        userStorage.findByEmail(userDto.email())
                .ifPresent(existingUser -> {
                    throw new ConflictException("Пользователь с email='" + userDto.email() + "' уже существует");
                });

        // Конвертация DTO в сущность и сохранение
        User user = userMapper.toEntity(userDto);
        return userMapper.toUserDto(userStorage.createUser(user));
    }

    // Обновление пользователя
    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userDto) {
        // Проверка существования пользователя
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        // Проверка уникальности email
        if (userDto.email() != null && !userDto.email().isBlank()) {
            userStorage.findByEmail(userDto.email())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw new ConflictException("Пользователь с email='" + userDto.email() + "' уже существует");
                        }
                    });
        }

        // Обновление и сохранение
        userMapper.updateEntity(userDto, user);
        return userMapper.toUserDto(userStorage.updateUser(user));
    }

    // Удаление пользователя
    @Override
    public void deleteUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        userStorage.deleteUser(userId);
    }

    // Получение пользователя по ID
    @Override
    public UserDto getUserById(Long userId) {
        // Проверка существования пользователя
        return userStorage.getUserById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    // Получение всех пользователей
    @Override
    public List<UserDto> getAllUsers() {
        // Конвертирование в DTO
        return userStorage.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    // Получение пользователя или выброс исключения
    @Override
    public User getUserOrThrow(Long userId) {
        // Поиск пользователя с выбросом исключения, если не найден
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
