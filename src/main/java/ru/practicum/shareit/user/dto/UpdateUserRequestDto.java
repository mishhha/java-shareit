package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {

    private String email;
    private String name;

}
