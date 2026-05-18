package ru.practicum.shareit.user;

import ru.practicum.user.User;

import java.util.List;

public interface UserService {

    List<ru.practicum.user.User> getAllUsers();
    ru.practicum.user.User saveUser(User user);

}
