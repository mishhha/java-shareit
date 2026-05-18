package ru.practicum.shareit.user;

import ru.practicum.user.User;

import java.util.List;

public interface UserRepository {
    List<ru.practicum.user.User> findAll();
    ru.practicum.user.User save(User user);
}
