package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {

    private Long id;
    private Long itemId;
    private Long ownerId;
    private Long userId;
    private LocalDate bookingStart;
    private LocalDate bookingFinish;

}
