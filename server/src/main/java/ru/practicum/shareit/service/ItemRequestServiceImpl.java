package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.dto.request.ItemRequestBodyDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.model.ItemRequest;
import ru.practicum.shareit.repository.ItemRequestRepository;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestStorage;
    private final UserRepositoryJpa userRepositoryJpa;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto save(ItemRequestBodyDto requestBodyDto, Long requesterId) {

        User findUser = userRepositoryJpa.findById(requesterId)
            .orElseThrow(() -> new NotFoundException("Пользователь с ID " + requesterId + " не найден"));

        ItemRequest newItemRequest = mapper.mapToItemRequest(requestBodyDto);

        newItemRequest.setRequester(findUser);

        ItemRequest itemRequest = itemRequestStorage.save(newItemRequest);

        return mapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllYourRequestsById(Long requesterId) {

        boolean findUser = userRepositoryJpa.existsById(requesterId);

        if (!findUser) {
            throw new NotFoundException("Пользователь с ID " + requesterId + " не найден");
        }

        return itemRequestStorage.findByRequesterIdWithItems(requesterId).stream()
            .map(mapper::mapToItemRequestDto)
            .toList();

    }

    public List<ItemRequestDto> findAllRequests(Long requesterId) {
        return itemRequestStorage.findAllByOrderByDateTimeDesc(requesterId).stream()
            .map(mapper::mapToItemRequestDto)
            .toList();
    }

    public ItemRequestDto findItemRequestById(Long requestId) {
        Optional<ItemRequest> request = itemRequestStorage.findItemRequestById(requestId);
        if (request.isEmpty()) {
            throw new NotFoundException("Запрос с ID " + requestId + " не найден.");
        }
        return mapper.mapToItemRequestDto(request.get());
    }

}
