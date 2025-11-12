package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestCreateDtoTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @Test
    void serializeShouldHandleDescription() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Test desc");

        assertThat(json.write(dto)).extractingJsonPathStringValue("$.description").isEqualTo("Test desc");
    }
}
