package ru.practicum.shareit.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepositoryJpa repository;

    @Mock
    private ItemRepositoryJpa itemRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveUser_shouldCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(repository.existsUserByEmail("ivan@test.ru")).thenReturn(false);
        when(userMapper.mapToUser(dto)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(userMapper.mapToUserDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.saveUser(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).save(user);
    }

    @Test
    void saveUser_withExistingEmail_shouldThrowException() {
        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");

        when(repository.existsUserByEmail("ivan@test.ru")).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            userService.saveUser(dto);
        });
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user = new User();
        user.setId(1L);

        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);

        when(repository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(dto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");
        dto.setItemList(List.of());

        when(repository.findById(1L))
            .thenReturn(Optional.of(user));

        when(itemRepository.findByUserId(1L))
            .thenReturn(List.of());

        when(userMapper.mapToUserDto(user))
            .thenReturn(dto);

        UserResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_withNonExistentId_shouldThrowException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void updateUser_shouldUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Иван Новый");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setName("Иван Новый");
        responseDto.setEmail("ivan@test.ru");

        when(repository.findById(1L))
            .thenReturn(Optional.of(user));

        when(repository.save(user))
            .thenReturn(user);

        when(userMapper.mapToUserDto(user))
            .thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(1L, dto);

        assertNotNull(result);
        assertEquals("Иван Новый", result.getName());
    }

    @Test
    void updateUser_withNonExistentId_shouldThrowException() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, dto);
        });
    }

    @Test
    void delete_shouldRemoveUser() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        userService.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_withNonExistentId_shouldThrowException() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            userService.delete(999L);
        });
    }
}