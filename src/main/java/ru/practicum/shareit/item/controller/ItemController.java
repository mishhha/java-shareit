package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> findUserItems(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long ownerId) {
        return itemService.userItems(ownerId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto findItemById(@Positive @PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> searchItemForBooking(@RequestParam() String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long ownerId,
        @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemService.create(ownerId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto update(@Positive @PathVariable Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                  @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        return itemService.update(itemId, ownerId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@Positive @PathVariable Long itemId,
                                            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                            @Valid @RequestBody CommentRequestDto comment
                                            ) {
        return itemService.createComment(itemId, userId, comment);
    }

}