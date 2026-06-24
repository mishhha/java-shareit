package ru.practicum.shareit.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBookingDto {

    @NotNull(message = "ID вещи не может быть null")
    private Long itemId;
    @NotNull(message = "Дата начала не может быть null")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания не может быть null")
    private LocalDateTime end;

}