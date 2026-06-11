package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    // Поиск оформленных пользователем бронирований.

    @Query("select b from Booking as b where b.booker.id = ?1 order by b.start desc")
    List<Booking> findBookingsUserById(Long userId);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.end >= ?2 and b.start <= ?2 order by b.start desc")
    List<Booking> findCurrentBookingsUserById(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findPastBookingsUserById(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findFutureBookingsUserById(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findBookingsUserByIdAndStatus(Long userId, BookingStatus status);

    // Поиск забронированных вещей пользователя.

    @Query("select b from Booking as b where b.item.user.id = ?1 order by b.start desc")
    List<Booking> findBookingsForItemsByUserId(Long userId);

    @Query("select b from Booking as b where b.item.user.id = ?1 " +
        "AND b.start <= ?2 AND b.end >= ?2 order by b.start desc")
    List<Booking> findCurrentBookingsForItemsByUserId(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.item.user.id = ?1 " +
        "AND b.end < ?2 order by b.start desc")
    List<Booking> findPastBookingsForItemsByUserId(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.item.user.id = ?1 " +
        "AND b.start > ?2 order by b.start desc")
    List<Booking> findFutureBookingsForItemsByUserId(Long userId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.item.user.id = ?1 " +
        "AND b.status = ?2 order by b.start desc")
    List<Booking> findBookingsForItemsByUserIdAndStatus(Long userId, BookingStatus status);

    // Поиск бронирования конкретной вещи

    @Query("select b from Booking as b where b.item.id = ?1 and b.end <= ?2 order by b.end desc")
    Optional<Booking> findLastEndBookingByItemId(Long itemId, LocalDateTime dateTime);

    @Query("select b from Booking as b where b.item.id = ?1 and b.start >= ?2 order by b.start asc")
    Optional<Booking> findNextBookingByItemId(Long itemId, LocalDateTime dateTime);

    // Поиск бронирования вещи по статусу

    @Query("select COUNT(b) > 0 from Booking as b " +
        "where b.item.id = ?1 and b.booker.id = ?2 " +
        "and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED and b.end < ?3")
    boolean existsApprovedCompletedBooking(Long itemId, Long bookerId, LocalDateTime dateTime);

}