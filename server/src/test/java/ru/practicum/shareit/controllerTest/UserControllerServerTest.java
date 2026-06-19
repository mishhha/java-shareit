package ru.practicum.shareit.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.controller.UserController;
import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturn200() throws Exception {

        UserResponseDto user = new UserResponseDto();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {

        UserResponseDto user = new UserResponseDto();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Иван"))
            .andExpect(jsonPath("$.email").value("ivan@test.ru"));

        verify(userService).getUserById(1L);
    }

    @Test
    void saveNewUser_shouldReturn200() throws Exception {

        UserResponseDto createdUser = new UserResponseDto();
        createdUser.setId(1L);
        createdUser.setName("Иван");
        createdUser.setEmail("ivan@test.ru");

        when(userService.saveUser(any())).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван\",\"email\":\"ivan@test.ru\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Иван"))
            .andExpect(jsonPath("$.email").value("ivan@test.ru"));

        verify(userService).saveUser(any(NewUserRequestDto.class));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {

        UserResponseDto updatedUser = new UserResponseDto();
        updatedUser.setId(1L);
        updatedUser.setName("Иван Новый");
        updatedUser.setEmail("ivan.new@test.ru");

        when(userService.updateUser(anyLong(), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Иван Новый\",\"email\":\"ivan.new@test.ru\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Иван Новый"))
            .andExpect(jsonPath("$.email").value("ivan.new@test.ru"));

        verify(userService).updateUser(eq(1L), any(UpdateUserRequestDto.class));
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {

        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}