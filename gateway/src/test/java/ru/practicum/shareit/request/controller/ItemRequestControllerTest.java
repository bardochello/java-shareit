package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient client;

    @Test
    void addRequestWhenValidThenOk() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Desc");
        when(client.addRequest(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addRequestWhenBlankDescriptionThenBadRequest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRequestWhenNullDescriptionThenBadRequest() throws Exception {
        String jsonWithNullDescription = "{\"description\": null}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullDescription))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsByUserThenOk() throws Exception {
        when(client.getRequestsByUser(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsWhenValidPaginationThenOk() throws Exception {
        when(client.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsWhenNegativeFromThenBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all?from=-1&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestByIdThenOk() throws Exception {
        when(client.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}
