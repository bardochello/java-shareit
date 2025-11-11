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
    void createUser_shouldSaveAndReturnDto() {
        UserDto result = userService.createUser(createDto);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("User");
        assertThat(result.email()).isEqualTo("user@email.com");
    }

    @Test
    void createUser_duplicateEmail_shouldThrowConflict() {
        userService.createUser(createDto);

        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        UserDto created = userService.createUser(createDto);
        UserUpdateDto updateDto = new UserUpdateDto("New Name", "new@email.com");

        UserDto result = userService.updateUser(created.id(), updateDto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.email()).isEqualTo("new@email.com");
    }

    @Test
    void updateUser_duplicateEmail_shouldThrowConflict() {
        userService.createUser(createDto);
        UserCreateDto other = new UserCreateDto("Other", "other@email.com");
        UserDto otherCreated = userService.createUser(other);

        UserUpdateDto updateDto = new UserUpdateDto(null, "user@email.com");

        assertThatThrownBy(() -> userService.updateUser(otherCreated.id(), updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void updateUser_invalidId_shouldThrowNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto("New", null);

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserDto created = userService.createUser(createDto);

        userService.deleteUser(created.id());

        assertThat(userRepository.findById(created.id())).isEmpty();
    }

    @Test
    void getUserById_shouldReturnDto() {
        UserDto created = userService.createUser(createDto);

        UserDto result = userService.getUserById(created.id());

        assertThat(result).isEqualTo(created);
    }

    @Test
    void getUserById_invalidId_shouldThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getAllUsers_shouldReturnList() {
        userService.createUser(createDto);
        UserCreateDto other = new UserCreateDto("Other", "other@email.com");
        userService.createUser(other);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getUserOrThrow_shouldReturnUser() {
        UserDto created = userService.createUser(createDto);

        User result = userService.getUserOrThrow(created.id());

        assertThat(result.getId()).isEqualTo(created.id());
    }

    @Test
    void getUserOrThrow_invalidId_shouldThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserOrThrow(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }
}
