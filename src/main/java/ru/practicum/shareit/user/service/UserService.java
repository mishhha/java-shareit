package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long userId);

    UserResponseDto saveUser(NewUserRequestDto newUserRequestDto);

    UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto);

    void delete(Long userId);

}