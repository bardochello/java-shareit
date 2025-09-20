package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

// Репозиторий для пользователей
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
