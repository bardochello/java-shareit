package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserCreateDto createDto;

    @BeforeEach
    void setUp() {
        createDto = new UserCreateDto("User", "user@email.com");
    }

    @Test
    void createUserShouldSaveAndReturnDto() {
        UserDto result = userService.createUser(createDto);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("User");
        assertThat(result.email()).isEqualTo("user@email.com");
    }

    @Test
    void createUserWhenDuplicateEmailThenThrowConflict() {
        userService.createUser(createDto);

        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateUserShouldUpdateNameAndEmail() {
        UserDto created = userService.createUser(createDto);
        UserUpdateDto updateDto = new UserUpdateDto("New Name", "new@email.com");

        UserDto result = userService.updateUser(created.id(), updateDto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.email()).isEqualTo("new@email.com");
    }

    @Test
    void updateUserWhenDuplicateEmailThenThrowConflict() {
        userService.createUser(createDto);
        UserCreateDto other = new UserCreateDto("Other", "other@email.com");
        UserDto otherCreated = userService.createUser(other);

        UserUpdateDto updateDto = new UserUpdateDto(null, "user@email.com");

        assertThatThrownBy(() -> userService.updateUser(otherCreated.id(), updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateUserWhenInvalidIdThenThrowNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto("New", null);

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void deleteUserShouldRemoveUser() {
        UserDto created = userService.createUser(createDto);

        userService.deleteUser(created.id());

        assertThat(userRepository.findById(created.id())).isEmpty();
    }

    @Test
    void getUserByIdShouldReturnDto() {
        UserDto created = userService.createUser(createDto);

        UserDto result = userService.getUserById(created.id());

        assertThat(result).isEqualTo(created);
    }

    @Test
    void getUserByIdWhenInvalidIdThenThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getAllUsersShouldReturnList() {
        userService.createUser(createDto);
        UserCreateDto other = new UserCreateDto("Other", "other@email.com");
        userService.createUser(other);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getUserOrThrowShouldReturnUser() {
        UserDto created = userService.createUser(createDto);

        User result = userService.getUserOrThrow(created.id());

        assertThat(result.getId()).isEqualTo(created.id());
    }

    @Test
    void getUserOrThrowWhenInvalidIdThenThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserOrThrow(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }
}
