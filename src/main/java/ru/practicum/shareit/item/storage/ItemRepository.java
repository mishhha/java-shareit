package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> searchItemsForBooking(String text);

    List<Item> findAll();

    Item findById(Long itemId);

    Item create(Item item);

    Item update(Item item);

    void delete(Long itemId);

    List<Item> findItemsByOwnerId(Long ownerId);

}
