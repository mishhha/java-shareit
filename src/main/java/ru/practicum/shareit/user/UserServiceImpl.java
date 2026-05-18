package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.user.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<ru.practicum.user.User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public ru.practicum.user.User saveUser(User user) {
        return repository.save(user);
    }

}
