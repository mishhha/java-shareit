package ru.practicum.shareit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequestDto {

    @NotBlank(message = "Имя должно быть заполнено.")
    private String name;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным.")
    private String email;

}
