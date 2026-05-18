package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, ru.practicum.user.User> users = new HashMap<>();

    @Override
    public List<ru.practicum.user.User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public ru.practicum.user.User save(ru.practicum.user.User user) {
        if(user == null || users.containsKey(user.getId())) {
            return null;
        }
        ru.practicum.user.User newUser = new User();
        newUser.setId(user.getId());
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        users.put(user.getId(), newUser);
        return newUser;
    }

}
