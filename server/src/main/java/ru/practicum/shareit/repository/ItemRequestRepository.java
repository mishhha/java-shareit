package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterId(Long requesterId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.id = ?1")
    Optional<ItemRequest> findItemRequestById(Long id);

    @Query(
        "SELECT ir " +
        "FROM ItemRequest ir " +
        "LEFT JOIN FETCH ir.items " +
        "WHERE ir.requester.id = ?1 " +
        "ORDER BY ir.dateTime DESC"
    )
    List<ItemRequest> findByRequesterIdWithItems(Long requesterId);

    @Query("select ir from ItemRequest as ir WHERE ir.requester.id != ?1 ORDER BY ir.dateTime DESC")
    List<ItemRequest> findAllByOrderByDateTimeDesc(Long requesterId);

}
