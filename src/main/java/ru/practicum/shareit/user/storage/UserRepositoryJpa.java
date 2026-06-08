package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepositoryJpa extends JpaRepository<User, Long> {

    boolean existsUserByEmail(String email);

}
