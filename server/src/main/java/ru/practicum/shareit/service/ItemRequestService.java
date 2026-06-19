package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.request.ItemRequestBodyDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(ItemRequestBodyDto itemRequest, Long requesterId);

    List<ItemRequestDto> findAllYourRequestsById(Long requesterId);

    List<ItemRequestDto> findAllRequests(Long requesterId);

    ItemRequestDto findItemRequestById(Long requestId);

}