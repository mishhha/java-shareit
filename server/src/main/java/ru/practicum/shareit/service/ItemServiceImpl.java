package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.repository.CommentRepositoryJpa;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.model.ItemRequest;
import ru.practicum.shareit.repository.ItemRequestRepository;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ItemRequestRepository itemRequestStorage;
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

        boolean findUser = userRepository.existsById(ownerId);
        if (!findUser) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден.");
        }

        List<ItemResponseDto> items = itemRepository.findByUserId(ownerId).stream()
            .map(itemMapper::mapToItemDto)
            .toList();

        if (items.isEmpty()) {
            return items;
        }

        List<Long> itemIds = items.stream()
            .map(ItemResponseDto::getId)
            .toList();

        List<Booking> bookings = bookingRepository.findBookingsForItemsByUserId(ownerId);
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
            .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<CommentResponseDto>> commentsByItem = comments.stream()
            .collect(Collectors.groupingBy(
                c -> c.getItem().getId(),
                Collectors.mapping(itemMapper::mapToCommentDto, Collectors.toList())
            ));

        LocalDateTime dateTime = LocalDateTime.now();

        for (ItemResponseDto itemDto : items) {
            Long itemId = itemDto.getId();

            List<Booking> itemBookings = bookingsByItem.getOrDefault(itemDto.getId(), Collections.emptyList());

            Booking last = itemBookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.REJECTED)
                .filter(b -> b.getEnd().isBefore(dateTime))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

            Booking next = itemBookings.stream()
                .filter(b -> b.getStart().isAfter(dateTime))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

            if (last != null) {
                itemDto.setLastBooking(bookingMapper.mapToResponseBookingDto(last));
            }

            if (next != null) {
                itemDto.setNextBooking(bookingMapper.mapToResponseBookingDto(next));
            }

            itemDto.setComments(commentsByItem.getOrDefault(itemId, Collections.emptyList()));

        }

        return items;

    }

    @Override
    @Transactional
    public ItemResponseDto create(Long ownerId, NewItemRequestDto newItemRequestDto) {

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден"));

        Item item = itemMapper.mapToItem(newItemRequestDto);
        item.setUser(owner);

        if (newItemRequestDto.getRequestId() != null) {
            ItemRequest itemRequest =
                itemRequestStorage.findItemRequestById(newItemRequestDto.getRequestId())
                .orElseThrow(() ->
                    new NotFoundException("Запрос с ID " + newItemRequestDto.getRequestId() + " не найден."));

            item.setRequest(itemRequest);
        }

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
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));

        ItemResponseDto dto = itemMapper.mapToItemDto(item);

        List<CommentResponseDto> comments = commentRepository.findByItemId(itemId).stream()
            .map(itemMapper::mapToCommentDto)
            .toList();

        dto.setComments(comments);

        return dto;
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemId, Long ownerId, UpdateItemRequestDto updateItemRequestDto) {

        Item findItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));

        if (!findItem.getUser().getId().equals(ownerId)) {
            throw new ValidationException("Обновлять данные предмета может только владелец.");
        }

        if (updateItemRequestDto.getName() != null) {
            findItem.setName(updateItemRequestDto.getName());
        }

        if (updateItemRequestDto.getDescription() != null) {
            findItem.setDescription(updateItemRequestDto.getDescription());
        }

        if (updateItemRequestDto.getAvailable() != null) {
            findItem.setAvailable(updateItemRequestDto.getAvailable());
        }

        Item updateItem = itemRepository.save(findItem);

        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {

        itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));

        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long itemId, Long userId, CommentRequestDto dto) {

        Item findItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Предмет с ID " + itemId + " не найден"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime dateTime = LocalDateTime.now();

        boolean hasRented = bookingRepository.existsApprovedCompletedBooking(itemId, userId, dateTime);

        if (!hasRented) {
            throw new ValidationException("Оставлять отзыв может только арендатор.");
        }

        Comment newComment = itemMapper.mapToComment(dto);
        newComment.setCreated(LocalDateTime.now());
        newComment.setItem(findItem);
        newComment.setUser(user);

        Comment comment = commentRepository.save(newComment);

        return itemMapper.mapToCommentDto(comment);
    }


}