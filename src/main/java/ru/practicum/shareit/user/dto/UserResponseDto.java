package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private List<ItemResponseDto> itemList;

}
