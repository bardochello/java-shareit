package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemCreateDtoTest {

    @Autowired
    private JacksonTester<ItemCreateDto> json;

    @Test
    void serializeShouldHandleFields() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("Name", "Desc", true, 1L);

        assertThat(json.write(dto)).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(json.write(dto)).extractingJsonPathStringValue("$.description").isEqualTo("Desc");
        assertThat(json.write(dto)).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(json.write(dto)).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
