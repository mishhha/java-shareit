package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponseDto> searchItemsForBooking(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.searchItemsForBooking(text.toLowerCase());

        return items.stream()
            .map(itemMapper::mapToItemDto)
            .toList();
    }

    @Override
    public List<ItemResponseDto> userItems(Long ownerId) {

        User owner = userRepository.findById(ownerId);
        if (owner == null) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        return itemRepository.findItemsByOwnerId(ownerId).stream()
            .map(itemMapper::mapToItemDto)
            .toList();
    }

    @Override
    public ItemResponseDto create(Long ownerId, NewItemRequestDto newItemRequestDto) {

        User owner = userRepository.findById(ownerId);
        if (owner == null) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        Item item = itemMapper.mapToItem(newItemRequestDto);
        item.setOwnerId(ownerId);

        Item newItem = itemRepository.create(item);
        return itemMapper.mapToItemDto(newItem);
    }

    @Override
    public List<ItemResponseDto> findAll() {
        return itemRepository.findAll().stream()
            .map(itemMapper::mapToItemDto)
            .toList();
    }

    @Override
    public ItemResponseDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto update(Long itemId, Long ownerId, UpdateItemRequestDto updateItemRequestDto) {

        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Обновлять данные предмета может только владелец.");
        }

        if (updateItemRequestDto.getName() != null) {
            item.setName(updateItemRequestDto.getName());
        }

        if (updateItemRequestDto.getDescription() != null) {
            item.setDescription(updateItemRequestDto.getDescription());
        }

        if (updateItemRequestDto.getAvailable() != null) {
            item.setAvailable(updateItemRequestDto.getAvailable());
        }

        Item updateItem = itemRepository.update(item);

        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    public void delete(Long itemId) {

        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        itemRepository.delete(itemId);
    }
}
