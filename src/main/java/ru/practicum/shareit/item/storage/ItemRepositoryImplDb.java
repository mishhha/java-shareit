package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ItemRepositoryImplDb implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {
        item.setId(nextGenId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updateItem) {
        items.put(updateItem.getId(), updateItem);
        return updateItem;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> findItemsByOwnerId(Long ownerId) {
        return items.values().stream()
            .filter(item -> item.getOwnerId().equals(ownerId))
            .toList();
    }

    @Override
    public List<Item> searchItemsForBooking(String text) {
        return items.values().stream()
            .filter(item -> (
                item.getName().toLowerCase().contains(text) ||
                item.getDescription().toLowerCase().contains(text)
            ) && Boolean.TRUE.equals(item.getAvailable()))
            .toList();
    }

    public Long nextGenId() {
        return items.keySet().stream()
            .max(Long::compareTo)
            .map(id -> id + 1)
            .orElse(1L);
    }

}
