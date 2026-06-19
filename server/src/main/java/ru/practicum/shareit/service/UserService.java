package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.user.*;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long userId);

    UserResponseDto saveUser(NewUserRequestDto newUserRequestDto);

    UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto);

    void delete(Long userId);

}