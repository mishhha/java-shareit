package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.request.ItemRequestBodyDto;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(@Value("${server.base-url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestBodyDto dto) {
        return post("/requests", userId, dto);
    }

    public ResponseEntity<Object> getAllUserRequests(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllOtherRequests(Long userId) {
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId) {
       return get("/requests/" + requestId);
    }

}