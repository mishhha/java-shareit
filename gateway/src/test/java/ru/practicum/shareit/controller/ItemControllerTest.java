package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.client.ItemClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void findUserItems_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemClient.getAllByUserId(anyLong()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk());

        verify(itemClient).getAllByUserId(5L);
    }

    @Test
    void findItemById_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemClient.getById(1L))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk());

        verify(itemClient).getById(1L);
    }

    @Test
    void searchItemForBooking_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemClient.search(anyString()))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/items/search")
                .param("text", "дрель"))
            .andExpect(status().isOk());

        verify(itemClient).search("дрель");
    }

    @Test
    void create_shouldReturn201() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(201)
            .body(null);

        when(itemClient.create(anyLong(), any()))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Дрель\",\"description\":\"Описание\",\"available\":true}"))
            .andExpect(status().isCreated());

        verify(itemClient).create(eq(5L), any());
    }

    @Test
    void create_withEmptyName_shouldReturn400() throws Exception {

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"description\":\"Описание\",\"available\":true}"))
            .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void update_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(itemClient.update(anyLong(), anyLong(), any())).thenReturn(mockResponse);

        mockMvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Новое имя\"}"))
            .andExpect(status().isOk());

        verify(itemClient).update(eq(1L), eq(5L), any());
    }

    @Test
    void delete_shouldReturn204() throws Exception {

        doNothing().when(itemClient).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/items/1"))
            .andExpect(status().isNoContent());

        verify(itemClient).delete(null, 1L);
    }

    @Test
    void createComment_shouldReturn201() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(201)
            .body(null);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"Отлично!\"}"))
            .andExpect(status().isCreated());

        verify(itemClient).createComment(eq(1L), eq(5L), any());
    }
}