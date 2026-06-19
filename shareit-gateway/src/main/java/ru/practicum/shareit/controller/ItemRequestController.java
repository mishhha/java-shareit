package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.request.ItemRequestBodyDto;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> save(
        @RequestHeader (value = "X-Sharer-User-Id") @Positive Long requesterId,
        @Valid @RequestBody ItemRequestBodyDto dto
    ) {
        return itemRequestClient.createRequest(requesterId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllYourRequestsById(
        @RequestHeader (value = "X-Sharer-User-Id") @Positive Long requesterId
    ) {
        return itemRequestClient.getAllUserRequests(requesterId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllRequests(
        @RequestHeader("X-Sharer-User-Id") Long requesterId
    ) {
        return itemRequestClient.getAllOtherRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findByItemRequestId(@Positive @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(requestId);
    }

}
