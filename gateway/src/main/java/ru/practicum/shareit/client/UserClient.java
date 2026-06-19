package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;

@Component
public class UserClient extends BaseClient {

    public UserClient(@Value("${server.base-url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users");
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/users/" + userId);
    }

    public ResponseEntity<Object> createUser(NewUserRequestDto dto) {
        return post("/users", dto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UpdateUserRequestDto dto) {
        return patch("/users/" + userId, userId, dto);
    }

    public void deleteUser(Long userId) {
        delete("/users/" + userId, userId);
    }

}