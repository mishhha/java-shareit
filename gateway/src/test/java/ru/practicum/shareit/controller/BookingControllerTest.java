package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;

import java.time.LocalDateTime;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking_success_shouldReturn201AndBooking() throws Exception {

        RequestBookingDto requestDto = new RequestBookingDto();
        requestDto.setItemId(10L);
        requestDto.setStart(java.time.LocalDateTime.now().plusDays(1));
        requestDto.setEnd(java.time.LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(201)
            .body("Тело ответа от Server");

        when(bookingClient.createBooking(anyLong(), any(RequestBookingDto.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/bookings")
            .header("X-Sharer-User-Id", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"itemId\":10,\"start\":\"2026-06-20T10:00:00\",\"end\":\"2026-06-25T10:00:00\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().string("Тело ответа от Server"));

        verify(bookingClient).createBooking(eq(5L), any(RequestBookingDto.class));
    }

    @Test
    void shouldChangeBookingStatus() throws Exception {

        ResponseEntity<Object> mokResponse = ResponseEntity
            .status(200)
            .body("APPROVED");

        when(bookingClient.updateBookingStatus(anyLong(), anyLong(),any()))
            .thenReturn(mokResponse);

        mockMvc.perform(patch("/bookings/1")
                .header("X-Sharer-User-Id", 5L)
                .param("approved", "true"))
            .andExpect(status().isOk());

        verify(bookingClient).updateBookingStatus(5L, 1L, true);

    }

    @Test
    void shouldReturnCorrectBookingAndStatus200() throws Exception {

        ResponseBookingDto dto = new ResponseBookingDto();
            dto.setId(1L);
            dto.setItem(null);
            dto.setBooker(null);
            dto.setStart(LocalDateTime.now().plusDays(1));
            dto.setEnd(LocalDateTime.now().plusDays(2));
            dto.setStatus(BookingStatus.REJECTED);

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(dto);

        when(bookingClient.getBookingById(anyLong(), anyLong()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/1")
            .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk());

        verify(bookingClient).getBookingById(5L, 1L);
    }

    @Test
    void shouldReturnUserBoookingsAndStatus200() throws Exception {

        ResponseEntity<Object> response = ResponseEntity
            .status(200)
            .body("Объект ResponseBookingDto");

        when(bookingClient.getAllBookings(anyLong(), anyString()))
            .thenReturn(response);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 5L)
                .param("state", "ALL"))
            .andExpect(status().isOk());

        verify(bookingClient).getAllBookings(5L, "ALL");

    }

    @Test
    void shouldReturnBookingsByOwnerAndStatus200() throws Exception {

        ResponseEntity<Object> response = ResponseEntity
            .status(200)
            .body("Объект ResponseBookingDto");

        when(bookingClient.getAllBookingsByOwner(anyLong(), anyString()))
            .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 5L)
                .param("state", "ALL"))
            .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByOwner(5L, "ALL");

    }


}