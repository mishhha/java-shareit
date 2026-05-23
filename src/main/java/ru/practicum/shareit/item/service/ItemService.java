package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> searchItemsForBooking(String text);

    List<ItemResponseDto> userItems(Long ownerId);

    ItemResponseDto create(Long ownerId, NewItemRequestDto newItemRequestDto);

    List<ItemResponseDto> findAll();

    ItemResponseDto findById(Long itemId);

    ItemResponseDto update(Long itemId, Long ownerId, UpdateItemRequestDto updateItemRequestDto);

    void delete(Long itemId);

}
