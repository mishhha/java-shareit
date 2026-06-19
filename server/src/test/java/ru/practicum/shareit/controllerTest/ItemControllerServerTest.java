package ru.practicum.shareit.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.controller.ItemController;
import ru.practicum.shareit.dto.item.CommentResponseDto;
import ru.practicum.shareit.dto.item.ItemResponseDto;
import ru.practicum.shareit.dto.item.NewItemRequestDto;
import ru.practicum.shareit.dto.item.UpdateItemRequestDto;
import ru.practicum.shareit.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void findUserItems_shouldReturn200() throws Exception {

        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);

        when(itemService.userItems(5L))
            .thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(itemService).userItems(5L);
    }

    @Test
    void findItemById_shouldReturn200() throws Exception {

        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemService.findById(1L))
            .thenReturn(item);

        mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Дрель"));

        verify(itemService).findById(1L);
    }

    @Test
    void searchItemForBooking_shouldReturn200() throws Exception {

        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);

        when(itemService.searchItemsByText("дрель"))
            .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                .param("text", "дрель"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(itemService).searchItemsByText("дрель");
    }

    @Test
    void create_shouldReturn200() throws Exception {

        ItemResponseDto createdItem = new ItemResponseDto();
        createdItem.setId(1L);
        createdItem.setName("Дрель");

        when(itemService.create(anyLong(), any()))
            .thenReturn(createdItem);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Дрель\",\"description\":\"Описание\",\"available\":true}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Дрель"));

        verify(itemService).create(eq(5L), any(NewItemRequestDto.class));
    }

    @Test
    void update_shouldReturn200() throws Exception {

        ItemResponseDto updatedItem = new ItemResponseDto();
        updatedItem.setId(1L);
        updatedItem.setName("Обновлённая дрель");

        when(itemService.update(anyLong(), anyLong(), any()))
            .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Обновлённая дрель\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Обновлённая дрель"));

        verify(itemService).update(eq(1L), eq(5L), any(UpdateItemRequestDto.class));
    }

    @Test
    void delete_shouldReturn200() throws Exception {

        doNothing().when(itemService)
            .delete(anyLong());

        mockMvc.perform(delete("/items/1"))
            .andExpect(status().isOk());

        verify(itemService).delete(1L);
    }

    @Test
    void createComment_shouldReturn200() throws Exception {

        CommentResponseDto comment = new CommentResponseDto();
        comment.setId(1L);
        comment.setText("Отличная вещь!");

        when(itemService.createComment(anyLong(), anyLong(), any()))
            .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"Отличная вещь!\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.text").value("Отличная вещь!"));

        verify(itemService).createComment(eq(1L), eq(5L), any());
    }
}