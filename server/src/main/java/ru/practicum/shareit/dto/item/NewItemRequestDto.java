package ru.practicum.shareit.dto.item;

import lombok.Data;

@Data
public class NewItemRequestDto {

    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

}
