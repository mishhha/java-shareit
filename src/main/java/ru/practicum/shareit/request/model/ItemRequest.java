package ru.practicum.shareit.request.model;

import lombok.Data;

@Data
public class ItemRequest {

    Long idRequest;
    String description;
    Long idRequestOwner;

}
