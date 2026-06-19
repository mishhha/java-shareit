package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserResponseDto saveNewUser(@RequestBody NewUserRequestDto newUserRequestDto) {
        return userService.saveUser(newUserRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable Long userId,
                                      @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        return userService.updateUser(userId, updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }

}