package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.item.*;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findUserItems(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long ownerId) {
        return itemClient.getAllByUserId(ownerId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findItemById(@Positive @PathVariable Long itemId) {
        return itemClient.getById(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchItemForBooking(@RequestParam() String text) {
        return itemClient.search(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long ownerId,
        @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemClient.create(ownerId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@Positive @PathVariable Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                  @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        return itemClient.update(itemId, ownerId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long itemId) {
        itemClient.delete(null, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@Positive @PathVariable Long itemId,
                                            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                            @Valid @RequestBody CommentRequestDto comment
                                            ) {
        return itemClient.createComment(itemId, userId, comment);
    }

}