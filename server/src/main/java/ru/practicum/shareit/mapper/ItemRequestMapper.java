package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.request.ItemResponseFromRequestDto;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.dto.request.ItemRequestBodyDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest mapToItemRequest(ItemRequestBodyDto dto) {

        LocalDateTime dateTime = LocalDateTime.now();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setDateTime(dateTime);
        return itemRequest;
    }


    public ItemResponseFromRequestDto mapToResponseFromRequestDto(Item item) {
        ItemResponseFromRequestDto itemDto = new ItemResponseFromRequestDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setOwnerId(item.getUser().getId());

        return itemDto;
    }

    public ItemRequestDto mapToItemRequestDto(ItemRequest response) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(response.getId());
        dto.setDescription(response.getDescription());
        dto.setRequesterId(response.getRequester().getId());
        dto.setCreated(response.getDateTime());

        if (response.getItems() != null) {
            List<ItemResponseFromRequestDto> itemResponse = response.getItems().stream()
                .map(this::mapToResponseFromRequestDto)
                .toList();
            dto.setItems(itemResponse);
        }
        return dto;
    }

}
