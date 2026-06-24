package ru.practicum.shareit.dto.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBookingDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}