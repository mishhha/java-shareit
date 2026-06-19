package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepositoryJpa bookingRepositoryJpa;

    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Test
    void save_shouldCreateBooking() {

        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        ResponseBookingDto response = bookingService.save(dto, booker.getId());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(item.getId(), response.getItem().getId());
        assertEquals(booker.getId(), response.getBooker().getId());

        Booking savedBooking = bookingRepositoryJpa.findById(response.getId()).orElseThrow();
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }

    @Test
    void save_sameUserAsOwner_shouldThrowException() {
        // Подготовка данных
        User user = createUser("Пользователь", "user@test.ru");
        Item item = createItem(user, "Дрель", true);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        assertThrows(RuntimeException.class, () -> {
            bookingService.save(dto, user.getId());
        });
    }

    @Test
    void save_itemNotAvailable_shouldThrowException() {
        // Подготовка данных
        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", false);  // Недоступна

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        assertThrows(RuntimeException.class, () -> {
            bookingService.save(dto, booker.getId());
        });
    }

    @Test
    void approvedBooking_ownerApproved_shouldChangeStatus() {
        // Подготовка данных
        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        Booking booking = createBooking(booker, item, BookingStatus.WAITING);

        ResponseBookingDto response = bookingService.approvedBooking(owner.getId(), true, booking.getId());

        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());

        Booking updatedBooking = bookingRepositoryJpa.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void approvedBooking_ownerRejected_shouldChangeStatus() {

        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        Booking booking = createBooking(booker, item, BookingStatus.WAITING);

        ResponseBookingDto response = bookingService.approvedBooking(owner.getId(), false, booking.getId());

        assertNotNull(response);
        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void findBookingById_bookerShouldGetBooking() {
        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        Booking booking = createBooking(booker, item, BookingStatus.WAITING);

        ResponseBookingDto response = bookingService.findBookingById(booker.getId(), booking.getId());

        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());
    }

    @Test
    void findAllBookingsUserById_stateAll_shouldReturnAllBookings() {

        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        createBooking(booker, item, BookingStatus.WAITING);
        createBooking(booker, item, BookingStatus.APPROVED);


        List<ResponseBookingDto> bookings = bookingService.findAllBookingsUserById("ALL", booker.getId());

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllBookingsUserById_stateWaiting_shouldReturnOnlyWaiting() {

        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        createBooking(booker, item, BookingStatus.WAITING);
        createBooking(booker, item, BookingStatus.APPROVED);
        createBooking(booker, item, BookingStatus.REJECTED);

        List<ResponseBookingDto> bookings = bookingService.findAllBookingsUserById("WAITING", booker.getId());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void findBookingsForItemsByUserId_stateAll_shouldReturnAllBookings() {

        User booker = createUser("Бронировщик", "booker@test.ru");
        User owner = createUser("Владелец", "owner@test.ru");
        Item item = createItem(owner, "Дрель", true);

        createBooking(booker, item, BookingStatus.WAITING);
        createBooking(booker, item, BookingStatus.APPROVED);

        List<ResponseBookingDto> bookings = bookingService.findBookingsForItemsByUserId(owner.getId(), "ALL");

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllBookingsUserById_invalidState_shouldThrowException() {

        User booker = createUser("Бронировщик", "booker@test.ru");

        assertThrows(RuntimeException.class, () -> {
            bookingService.findAllBookingsUserById("INVALID_STATE", booker.getId());
        });
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepositoryJpa.save(user);
    }

    private Item createItem(User owner, String name, Boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Описание");
        item.setAvailable(available);
        item.setUser(owner);
        return itemRepositoryJpa.save(item);
    }

    private Booking createBooking(User booker, Item item, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        return bookingRepositoryJpa.save(booking);
    }
}