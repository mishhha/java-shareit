package ru.practicum.shareit.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime dateTime;
    private List<ItemResponseFromRequestDto> responseFromRequestDtos;

}
