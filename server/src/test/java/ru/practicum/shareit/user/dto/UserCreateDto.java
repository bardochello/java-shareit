package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class UserCreateDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> json;

    @Test
    void serialize_shouldHandleEmail() throws Exception {
        UserCreateDto dto = new UserCreateDto("Name", "email@test.com");

        assertThat(json.write(dto)).extractingJsonPathStringValue("$.email").isEqualTo("email@test.com");
    }

    @Test
    void serialize_shouldHandleName() throws Exception {
        UserCreateDto dto = new UserCreateDto("Test Name", "email@test.com");

        assertThat(json.write(dto)).extractingJsonPathStringValue("$.name").isEqualTo("Test Name");
    }
}
