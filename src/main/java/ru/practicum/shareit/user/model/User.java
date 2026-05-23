package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
public class User {

    private Long id;
    private String email;
    private String name;
    private List<Item> itemList;

}
