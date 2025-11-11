package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BookingClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient client;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addBookingWhenValidThenOk() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto dto = new BookingCreateDto(1L, start, end);

        when(client.addBooking(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void addBookingWhenNullItemIdThenBadRequest() throws Exception {
        String jsonWithNullItemId = "{\"itemId\": null, \"start\": \"" +
                LocalDateTime.now().plusDays(1).format(formatter) +
                "\", \"end\": \"" +
                LocalDateTime.now().plusDays(2).format(formatter) + "\"}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullItemId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingWhenPastStartThenBadRequest() throws Exception {
        String jsonWithPastStart = "{\"itemId\": 1, \"start\": \"" +
                LocalDateTime.now().minusDays(1).format(formatter) +
                "\", \"end\": \"" +
                LocalDateTime.now().plusDays(2).format(formatter) + "\"}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithPastStart))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingWhenEndNotFutureThenBadRequest() throws Exception {
        String jsonWithNonFutureEnd = "{\"itemId\": 1, \"start\": \"" +
                LocalDateTime.now().plusDays(1).format(formatter) +
                "\", \"end\": \"" +
                LocalDateTime.now().minusDays(1).format(formatter) + "\"}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNonFutureEnd))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookingWhenEndBeforeStartThenOk() throws Exception {
        String jsonWithEndBeforeStart = "{\"itemId\": 1, \"start\": \"" +
                LocalDateTime.now().plusDays(2).format(formatter) +
                "\", \"end\": \"" +
                LocalDateTime.now().plusDays(1).format(formatter) + "\"}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithEndBeforeStart))
                .andExpect(status().isOk());
    }

    @Test
    void addBookingWhenMissingFieldsThenBadRequest() throws Exception {
        // Отсутствуют обязательные поля
        String jsonMissingFields = "{}";

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMissingFields))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBookingWhenTrueThenOk() throws Exception {
        when(client.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void approveBookingWhenFalseThenOk() throws Exception {
        when(client.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingByIdThenOk() throws Exception {
        when(client.getBookingById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByUserWhenAllStateThenOk() throws Exception {
        when(client.getBookingsByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByUserWhenDifferentStatesThenOk() throws Exception {
        when(client.getBookingsByUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings?state=CURRENT&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings?state=PAST&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings?state=FUTURE&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings?state=WAITING&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings?state=REJECTED&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwnerWhenAllStateThenOk() throws Exception {
        when(client.getBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwnerWhenDifferentStatesThenOk() throws Exception {
        when(client.getBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner?state=CURRENT&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner?state=PAST&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner?state=FUTURE&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner?state=WAITING&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner?state=REJECTED&from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}
