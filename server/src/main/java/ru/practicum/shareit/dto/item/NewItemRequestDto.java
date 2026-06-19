package ru.practicum.shareit.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequestDto {

    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;

}
