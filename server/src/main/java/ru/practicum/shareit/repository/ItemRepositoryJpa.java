package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
        "WHERE (LOWER(i.name) LIKE LOWER(?1) OR LOWER(i.description) LIKE LOWER(?1)) " +
        "AND i.available = true")
    List<Item> findItemByText(String text);

    List<Item> findByUserId(Long ownerId);


    @Query("select b from Booking as b where b.item.id = ?1 and b.end <= ?2 order by b.end desc")
    Optional<Booking> findLastEndBookingByItemId(Long itemId, LocalDateTime dateTime);
    }