package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.request.ItemRequestBodyDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(
        @RequestHeader("X-Sharer-User-Id") Long requesterId,
        @RequestBody ItemRequestBodyDto dto
    ) {
        return itemRequestService.save(dto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(
        @RequestHeader("X-Sharer-User-Id") Long requesterId
    ) {
        return itemRequestService.findAllYourRequestsById(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherRequests(
        @RequestHeader("X-Sharer-User-Id") Long requesterId
    ) {
        return itemRequestService.findAllRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {
        return itemRequestService.findItemRequestById(requestId);
    }
}