package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient client;

    @Test
    void addItemWhenValidWithRequestIdThenOk() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("Name", "Desc", true, 1L);
        when(client.addItem(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addItemWhenValidWithoutRequestIdThenOk() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("Name", "Desc", true, null);
        when(client.addItem(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addItemWhenInvalidNameThenBadRequest() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("", "Desc", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemWhenNullAvailableThenBadRequest() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("Name", "Desc", null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemWhenBlankDescriptionThenBadRequest() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("Name", "", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemWhenValidThenOk() throws Exception {
        ItemUpdateDto dto = new ItemUpdateDto("New", null, null);
        when(client.updateItem(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemWhenPartialDataThenOk() throws Exception {
        ItemUpdateDto dto = new ItemUpdateDto(null, "New Desc", false);
        when(client.updateItem(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItemThenOk() throws Exception {
        when(client.deleteItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemByIdThenOk() throws Exception {
        when(client.getItemById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwnerWhenValidPaginationThenOk() throws Exception {
        when(client.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwnerWhenNegativeFromThenBadRequest() throws Exception {
        mockMvc.perform(get("/items?from=-1&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsWhenValidTextThenOk() throws Exception {
        when(client.searchItems(anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search?text=test&from=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsWhenNoTextThenBadRequest() throws Exception {
        mockMvc.perform(get("/items/search?from=0&size=10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCommentWhenValidThenOk() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("Comment");
        when(client.addComment(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentWhenBlankTextThenBadRequest() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCommentWhenNullTextThenBadRequest() throws Exception {
        CommentCreateDto dto = new CommentCreateDto(null);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
