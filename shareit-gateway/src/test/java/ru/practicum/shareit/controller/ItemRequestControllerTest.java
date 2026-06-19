package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.client.ItemRequestClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void save_shouldReturn201() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(201)
            .body(null);

        when(itemRequestClient.createRequest(anyLong(), any()))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Нужна дрель\"}"))
            .andExpect(status().isCreated());

        verify(itemRequestClient).createRequest(eq(5L), any());
    }

    @Test
    void save_withEmptyDescription_shouldReturn400() throws Exception {

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"\"}"))
            .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(anyLong(), any());
    }

    @Test
    void findAllYourRequestsById_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemRequestClient.getAllUserRequests(anyLong()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk());

        verify(itemRequestClient).getAllUserRequests(5L);
    }

    @Test
    void findAllRequests_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemRequestClient.getAllOtherRequests(anyLong()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk());

        verify(itemRequestClient).getAllOtherRequests(5L);
    }

    @Test
    void findByItemRequestId_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemRequestClient.getRequestById(1L))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/requests/1"))
            .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(1L);
    }
}