package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Сущность пользователя
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // Уникальный идентификатор пользователя
    private Long id;

    // Имя пользователя
    private String name;

    // Email пользователя
    private String email;
}
