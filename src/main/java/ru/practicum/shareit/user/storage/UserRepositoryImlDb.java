package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImlDb implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public User save(User user) {
        if(user == null || users.containsKey(user.getId())) {
            return null;
        }
        User newUser = new User();
        newUser.setId(nextGenId());
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        if(!users.containsKey(userId)) {
            return;
        }
        users.remove(userId);
    }

    @Override
    public User update(User updateUser) {
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public boolean checkUserByEmail(String email) {
        return users.values().stream()
            .map(User::getEmail)
            .filter(Objects::nonNull)
            .anyMatch(storageEmail -> storageEmail.equalsIgnoreCase(email));
    }

    public Long nextGenId() {
        return users.keySet().stream()
            .max(Long::compareTo)
            .map(id -> id + 1)
            .orElse(1L);
    }

}
