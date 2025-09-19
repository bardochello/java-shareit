package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUser(Long userId);

    List<User> getAllUsers();

    boolean existsById(Long id);

    Optional<User> findByEmail(String email);
}
