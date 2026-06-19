package ru.practicum.shareit.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepositoryJpa bookingRepositoryJpa;

    @Mock
    private ItemRepositoryJpa itemRepositoryJpa;

    @Mock
    private UserRepositoryJpa userRepositoryJpa;

    @Mock
    private BookingMapper mapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void save_shouldCreateBooking() {
        User booker = new User();
        booker.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setUser(owner);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1L);

        when(userRepositoryJpa.findById(1L))
            .thenReturn(Optional.of(booker));

        when(itemRepositoryJpa.findById(10L))
            .thenReturn(Optional.of(item));

        when(mapper.mapToBooking(dto))
            .thenReturn(booking);

        when(bookingRepositoryJpa.save(any(Booking.class)))
            .thenReturn(booking);

        when(mapper.mapToResponseBookingDto(booking))
            .thenReturn(responseDto);

        ResponseBookingDto result = bookingService.save(dto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepositoryJpa).save(any(Booking.class));
    }

    @Test
    void save_withNonExistentUser_shouldThrowException() {
        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);

        when(userRepositoryJpa.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.save(dto, 999L);
        });
    }

    @Test
    void save_withNonExistentItem_shouldThrowException() {
        User booker = new User();
        booker.setId(1L);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);

        when(userRepositoryJpa.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepositoryJpa.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.save(dto, 1L);
        });
    }

    @Test
    void save_sameUserAsOwner_shouldThrowException() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setUser(user);
        item.setAvailable(true);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepositoryJpa.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepositoryJpa.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> {
            bookingService.save(dto, 1L);
        });
    }

    @Test
    void save_itemNotAvailable_shouldThrowException() {
        User booker = new User();
        booker.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setUser(owner);
        item.setAvailable(false);

        RequestBookingDto dto = new RequestBookingDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepositoryJpa.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepositoryJpa.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> {
            bookingService.save(dto, 1L);
        });
    }

    @Test
    void approvedBooking_shouldChangeStatusToApproved() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingRepositoryJpa.findById(1L)).thenReturn(Optional.of(booking));
        when(mapper.mapToResponseBookingDto(booking)).thenReturn(responseDto);

        ResponseBookingDto result = bookingService.approvedBooking(1L, true, 1L);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepositoryJpa).findById(1L);
    }

    @Test
    void approvedBooking_withNonExistentBooking_shouldThrowException() {
        when(bookingRepositoryJpa.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.approvedBooking(1L, true, 999L);
        });
    }

    @Test
    void findBookingById_shouldReturnBooking() {
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setUser(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        ResponseBookingDto responseDto = new ResponseBookingDto();
        responseDto.setId(1L);

        when(bookingRepositoryJpa.findById(1L)).thenReturn(Optional.of(booking));
        when(mapper.mapToResponseBookingDto(booking)).thenReturn(responseDto);

        ResponseBookingDto result = bookingService.findBookingById(2L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findBookingById_withNonExistentBooking_shouldThrowException() {
        when(bookingRepositoryJpa.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.findBookingById(1L, 999L);
        });
    }
}