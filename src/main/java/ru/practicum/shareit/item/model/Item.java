package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

// Сущность вещи
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    // Уникальный ID вещи
    private Long id;

    // Название вещи
    private String name;

    // Описание вещи
    private String description;

    // Статус доступности вещи
    private Boolean available;

    // Владелец вещи
    private User owner;

    // Идентификатор запроса, если вещь создана по запросу
    private Long requestId;
}
