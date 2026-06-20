package ru.practicum.shareit.dto.user;

import lombok.Data;

@Data
public class UpdateUserRequestDto {

    private String email;
    private String name;

}
