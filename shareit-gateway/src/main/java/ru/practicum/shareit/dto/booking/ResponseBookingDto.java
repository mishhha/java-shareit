package ru.practicum.shareit.dto.booking;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.dto.item.ItemResponseDto;
import ru.practicum.shareit.dto.user.UserResponseDto;

import java.time.LocalDateTime;

@Data
public class ResponseBookingDto {

    private Long id;
    private ItemResponseDto item;
    private UserResponseDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;

}
