package ru.practicum.shareit.dto.user;

import lombok.Data;
import ru.practicum.shareit.dto.item.ItemResponseDto;

import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private List<ItemResponseDto> itemList;

}
