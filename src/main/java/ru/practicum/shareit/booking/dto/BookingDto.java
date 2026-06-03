package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Long itemId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long ownerId;
    private Long userId;
    private LocalDate startReserved;
    private LocalDate finishReserved;

}
