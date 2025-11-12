package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentCreateDtoTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Test
    void serializeShouldHandleText() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("Test comment");

        assertThat(json.write(dto)).extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
    }
}
