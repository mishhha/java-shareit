package ru.practicum.shareit.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.controller.BookingController;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.service.BookingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void bookingItemRequest_shouldReturn201() throws Exception {

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1L);

        when(bookingService.save(any(), anyLong()))
            .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemId\":10,\"start\":\"2026-06-20T10:00:00\",\"end\":\"2026-06-25T10:00:00\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).save(any(), eq(5L));
    }

    @Test
    void bookingApproved_shouldReturn200() throws Exception {

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1L);

        when(bookingService.approvedBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                .header("X-Sharer-User-Id", 5L)
                .param("approved", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).approvedBooking(eq(5L), eq(true), eq(1L));
    }

    @Test
    void findBookingById_shouldReturn200() throws Exception {

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1L);

        when(bookingService.findBookingById(anyLong(), anyLong()))
            .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).findBookingById(eq(5L), eq(1L));
    }

    @Test
    void findBookingsByUserId_shouldReturn200() throws Exception {

        ResponseBookingDto booking = new ResponseBookingDto();
        booking.setId(1L);

        when(bookingService.findAllBookingsUserById(anyString(), anyLong()))
            .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 5L)
                .param("state", "ALL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(bookingService).findAllBookingsUserById(eq("ALL"), eq(5L));
    }

    @Test
    void findBookingsByOwnerId_shouldReturn200() throws Exception {

        ResponseBookingDto booking = new ResponseBookingDto();
        booking.setId(1L);

        when(bookingService.findBookingsForItemsByUserId(anyLong(), anyString())).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 5L)
                .param("state", "ALL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(bookingService).findBookingsForItemsByUserId(eq(5L), eq("ALL"));
    }
}