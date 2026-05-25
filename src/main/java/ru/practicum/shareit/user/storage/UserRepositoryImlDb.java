package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImlDb implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {

        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return users.get(id);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть null.");
        }

        checkUserByEmail(user.getEmail());

        user.setId(nextGenId());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new ConflictException("Пользователь с ID " + userId + " не существует.");
        }
        users.remove(userId);
    }

    @Override
    public User update(User updateUser) {

        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public void checkUserByEmail(String email) {
        boolean exists = users.values().stream()
            .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));

        if (exists) {
            throw new ConflictException("Пользователь с EMAIL " + email + " уже существует.");
        }
    }

    public Long nextGenId() {
        return users.keySet().stream()
            .max(Long::compareTo)
            .map(id -> id + 1)
            .orElse(1L);
    }

}