package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findById(Long userId);

    User save(User user);

    void delete(Long userId);

    User update(User user);

    void checkUserByEmail(String email);
}
