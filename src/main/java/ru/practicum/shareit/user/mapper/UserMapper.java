package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import org.mapstruct.*;

// Маппер для преобразования между сущностью пользователя и DTO
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // Конвертация UserCreateDto в сущность User
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateDto dto);

    // Конвертация сущности User в UserDto
    UserDto toUserDto(User user);

    // Обновление сущности User из UserUpdateDto
    void updateEntity(UserUpdateDto dto, @MappingTarget User user);
}
