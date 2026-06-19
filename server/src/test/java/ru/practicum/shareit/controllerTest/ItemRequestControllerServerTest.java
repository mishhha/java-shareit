package ru.practicum.shareit.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.controller.ItemRequestController;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_shouldReturn200() throws Exception {

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");

        when(itemRequestService.save(any(), anyLong()))
            .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Нужна дрель\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.description").value("Нужна дрель"));

        verify(itemRequestService).save(any(), eq(5L));
    }

    @Test
    void getAllUserRequests_shouldReturn200() throws Exception {

        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);

        when(itemRequestService.findAllYourRequestsById(5L))
            .thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService).findAllYourRequestsById(5L);
    }

    @Test
    void getAllOtherRequests_shouldReturn200() throws Exception {

        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);

        when(itemRequestService.findAllRequests(5L))
            .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService).findAllRequests(5L);
    }

    @Test
    void getRequestById_shouldReturn200() throws Exception {

        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Нужна дрель");

        when(itemRequestService.findItemRequestById(1L))
            .thenReturn(request);

        mockMvc.perform(get("/requests/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.description").value("Нужна дрель"));

        verify(itemRequestService).findItemRequestById(1L);
    }
}