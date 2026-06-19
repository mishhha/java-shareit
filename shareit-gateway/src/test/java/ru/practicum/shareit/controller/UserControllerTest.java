package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.client.UserClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void getAllUsers_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(userClient.getAllUsers()).thenReturn(mockResponse);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(userClient.getUserById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk());

        verify(userClient).getUserById(1L);
    }

    @Test
    void saveNewUser_shouldReturn201() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(201)
            .body(null);

        when(userClient.createUser(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван\",\"email\":\"ivan@test.ru\"}"))
            .andExpect(status().isCreated());

        verify(userClient).createUser(any());
    }

    @Test
    void saveNewUser_withEmptyName_shouldReturn400() throws Exception {

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"ivan@test.ru\"}"))
            .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    void saveNewUser_withInvalidEmail_shouldReturn400() throws Exception {

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван\",\"email\":\"не-почта\"}"))
            .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {

        ResponseEntity<Object> mockResponse = ResponseEntity
            .status(200)
            .body(null);

        when(userClient.updateUser(anyLong(), any())).thenReturn(mockResponse);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван Новый\",\"email\":\"ivan.new@test.ru\"}"))
            .andExpect(status().isOk());

        verify(userClient).updateUser(eq(1L), any());
    }

    @Test
    void updateUser_withInvalidEmail_shouldReturn400() throws Exception {

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван\",\"email\":\"не-почта\"}"))
            .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {

        doNothing().when(userClient).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isNoContent());

        verify(userClient).deleteUser(1L);
    }
}