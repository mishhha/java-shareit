package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.item.*;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${server.base-url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getAllByUserId(Long userId) {
        return get("/items", userId);
    }

    public ResponseEntity<Object> getById(Long id) {
        return get("/items/" + id);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/items/search", null, parameters);
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequestDto dto) {
        return post("/items", userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, UpdateItemRequestDto dto) {
        return patch("/items/" + itemId, userId, dto);
    }

    public void delete(Long userId, Long itemId) {
        delete("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, CommentRequestDto comment) {
        return post("/items/" + itemId + "/comment", userId, comment);
    }
}