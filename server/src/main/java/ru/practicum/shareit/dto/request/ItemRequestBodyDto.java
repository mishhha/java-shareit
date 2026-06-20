package ru.practicum.shareit.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestBodyDto {

    private String description;
    private Long ownerId;
    private LocalDateTime dateTime;

}
