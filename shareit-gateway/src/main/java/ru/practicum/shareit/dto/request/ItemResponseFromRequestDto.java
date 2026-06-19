package ru.practicum.shareit.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemResponseFromRequestDto {

    private Long id;
    private Long ownerId;
    private String name;

}