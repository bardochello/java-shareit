package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

// Хранилище пользователей в памяти
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Создание нового пользователя
    @Override
    public User createUser(User user) {
        user.setId(idCounter.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    // Обновление пользователя
    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    // Получение пользователя по ID
    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    // Удаление пользователя
    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        users.remove(userId);
    }

    // Получение всех пользователей
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Проверка существования пользователя
    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    // Поиск пользователя по email
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }
}
