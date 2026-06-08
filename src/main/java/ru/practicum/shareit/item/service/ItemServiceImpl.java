package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepositoryJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepositoryJpa;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final BookingRepositoryJpa bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepositoryJpa commentRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponseDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String param = "%" + text.toLowerCase() + "%";

        List<Item> items = itemRepository.findItemByText(param);

        return items.stream()
            .map(itemMapper::mapToItemDto)
            .toList();
    }

    @Override
    public List<ItemResponseDto> userItems(Long ownerId) {

        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        LocalDateTime dateTime = LocalDateTime.now();

        List<ItemResponseDto> items = itemRepository.findByUserId(ownerId).stream()
            .map(itemMapper::mapToItemDto)
            .toList();

        for (ItemResponseDto dto : items) {
            Optional<Booking> nextBooking = bookingRepository.findNextBookingByItemId(dto.getId(), dateTime);
            Optional<Booking> lastBooking = bookingRepository.findLastEndBookingByItemId(dto.getId(), dateTime);
            if (nextBooking.isPresent()) {
                dto.setNextBooking(bookingMapper.mapToResponseBookingDto(nextBooking.get()));
            }
            if (lastBooking.isPresent()) {
                dto.setLastBooking(bookingMapper.mapToResponseBookingDto(lastBooking.get()));
            }
            List<CommentResponseDto> comments = commentRepository.findByItemId(dto.getId()).stream()
                .map(itemMapper::mapToCommentDto)
                .toList();
            dto.setComments(comments);
        }

        return items;

    }

    @Override
    @Transactional
    public ItemResponseDto create(Long ownerId, NewItemRequestDto newItemRequestDto) {

        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }

        Item item = itemMapper.mapToItem(newItemRequestDto);
        item.setUser(owner.get());

        Item newItem = itemRepository.save(item);
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
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        ItemResponseDto dto = itemMapper.mapToItemDto(item.get());

        List<CommentResponseDto> comments = commentRepository.findByItemId(itemId).stream()
            .map(itemMapper::mapToCommentDto)
            .toList();

        dto.setComments(comments);

        return dto;
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemId, Long ownerId, UpdateItemRequestDto updateItemRequestDto) {

        Optional<Item> findItem = itemRepository.findById(itemId);
        if (findItem.isEmpty()) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        Item item = findItem.get();

        if (!item.getUser().getId().equals(ownerId)) {
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

        Item updateItem = itemRepository.save(item);

        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {

        Optional<Item> findItem = itemRepository.findById(itemId);
        if (findItem.isEmpty()) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long itemId, Long userId, CommentRequestDto dto) {

        Optional<Item> findItem = itemRepository.findById(itemId);
        if (findItem.isEmpty()) {
            throw new NotFoundException("Предмет с ID " + itemId + " не найден");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        LocalDateTime dateTime = LocalDateTime.now();

        boolean hasRented = bookingRepository.existsApprovedCompletedBooking(itemId, userId, dateTime);

        if (!hasRented) {
            throw new ValidationException("Оставлять отзыв может только арендатор.");
        }

        Comment newComment = itemMapper.mapToComment(dto);
        newComment.setCreated(LocalDateTime.now());
        newComment.setItem(findItem.get());
        newComment.setUser(user.get());

        Comment comment = commentRepository.save(newComment);

        return itemMapper.mapToCommentDto(comment);
    }


}