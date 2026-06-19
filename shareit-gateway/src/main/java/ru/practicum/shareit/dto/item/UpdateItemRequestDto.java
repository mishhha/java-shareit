package ru.practicum.shareit.dto.item;

import lombok.Data;

@Data
public class UpdateItemRequestDto {

    private String name;
    private String description;
    private Boolean available;

}