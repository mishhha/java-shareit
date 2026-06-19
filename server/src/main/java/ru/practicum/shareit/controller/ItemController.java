package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> findUserItems(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return itemService.userItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findItemById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItemForBooking(@RequestParam() String text) {
        return itemService.searchItemsByText(text);
    }

    @PostMapping
    public ItemResponseDto create(
        @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
        @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemService.create(ownerId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                  @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        return itemService.update(itemId, ownerId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@PathVariable Long itemId,
                                            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody CommentRequestDto comment
    ) {
        return itemService.createComment(itemId, userId, comment);
    }

}