package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public Item mapToItem(NewItemRequestDto newItemRequestDto) {
        Item item = new Item();
        item.setName(newItemRequestDto.getName());
        item.setDescription(newItemRequestDto.getDescription());
        item.setAvailable(newItemRequestDto.getAvailable());

        return item;
    }

    public ItemResponseDto mapToItemDto(Item item) {
        ItemResponseDto itemDto = new ItemResponseDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

}
