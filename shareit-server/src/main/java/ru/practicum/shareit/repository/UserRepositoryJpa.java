package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.model.User;

public interface UserRepositoryJpa extends JpaRepository<User, Long> {

    boolean existsById(Long userId);

    boolean existsUserByEmail(String email);

}
