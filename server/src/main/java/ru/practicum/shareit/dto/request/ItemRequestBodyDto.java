package ru.practicum.shareit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestBodyDto {

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private Long ownerId;
    private LocalDateTime dateTime;

}
