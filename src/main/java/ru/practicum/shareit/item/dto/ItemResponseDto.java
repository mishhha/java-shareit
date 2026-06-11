package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemResponseDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private ResponseBookingDto nextBooking;
    private ResponseBookingDto lastBooking;
    private List<CommentResponseDto> comments = new ArrayList<>();

}