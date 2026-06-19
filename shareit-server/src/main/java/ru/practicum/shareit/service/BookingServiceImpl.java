package ru.practicum.shareit.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepositoryJpa bookingRepositoryJpa;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    private final BookingMapper mapper;

    @Override
    @Transactional
    public ResponseBookingDto save(RequestBookingDto dto, Long bookerId) {

        Optional<User> findUser = userRepositoryJpa.findById(bookerId);
        if (findUser.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + bookerId + " не найден.");
        }

        User user = findUser.get();

        Optional<Item> findItem = itemRepositoryJpa.findById(dto.getItemId());
        if (findItem.isEmpty()) {
            throw new NotFoundException("Предмет с ID " + dto.getItemId() + " не найден.");
        }

        Item item = findItem.get();

        if (bookerId.equals(item.getUser().getId())) {
            throw new ValidationException("Нельзя бронировать свою же вещь.");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования.");
        }

        LocalDateTime dateTime = LocalDateTime.now().minusSeconds(10);

        if (dto.getStart().isBefore(dateTime) || dto.getEnd().isBefore(dateTime)) {
            throw new ValidationException("Нельзя назначить дату начала или окончания бронирования в прошлом.");
        }

        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new ValidationException("Бронирование не может закончиться раньше начала");
        }

        if (dto.getEnd().equals(dto.getStart())) {
            throw new ValidationException("Время бронирования не может быть равно 0");
        }

        Booking booking = mapper.mapToBooking(dto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking newBooking = bookingRepositoryJpa.save(booking);

        return mapper.mapToResponseBookingDto(newBooking);

    }

    @Override
    @Transactional
    public ResponseBookingDto approvedBooking(Long userId, Boolean approved, Long bookingId) {

        Optional<Booking> findBooking = bookingRepositoryJpa.findById(bookingId);
        if (findBooking.isEmpty()) {
            throw new NotFoundException("Бронирования с ID " + bookingId + " не найдено");
        }

        Booking booking = findBooking.get();

        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new ValidationException("Бронирование вещи может подтвердить только ее владелец");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return mapper.mapToResponseBookingDto(booking);

    }

    @Override
    public ResponseBookingDto findBookingById(Long userId, Long bookingId) {

        Optional<Booking> findBooking = bookingRepositoryJpa.findById(bookingId);
        if (findBooking.isEmpty()) {
            throw new NotFoundException("Бронирования с ID " + bookingId + " не найдено");
        }

        Booking booking = findBooking.get();

        if (!booking.getItem().getUser().getId().equals(userId) &&
            !booking.getBooker().getId().equals(userId)
        ) {
            throw new ValidationException("Информацию о бронировании может получить только арендатор или владелец.");
        }

        return mapper.mapToResponseBookingDto(booking);

    }

    @Override
    public List<ResponseBookingDto> findAllBookingsUserById(String state, Long userId) {

        LocalDateTime dateTime = LocalDateTime.now();

        boolean findUser = userRepositoryJpa.existsById(userId);
        if (!findUser) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL" :
                bookings = bookingRepositoryJpa.findBookingsUserById(userId);
                break;
            case "CURRENT" :
                bookings = bookingRepositoryJpa.findCurrentBookingsUserById(userId, dateTime);
                break;
            case "PAST" :
                bookings = bookingRepositoryJpa.findPastBookingsUserById(userId, dateTime);
                break;
            case "FUTURE" :
                bookings = bookingRepositoryJpa.findFutureBookingsUserById(userId, dateTime);
                break;
            case "WAITING" :
                bookings = bookingRepositoryJpa.findBookingsUserByIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED" :
                bookings = bookingRepositoryJpa.findBookingsUserByIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Не известное имя параметра " + state);
        }

        return bookings.stream()
            .map(mapper::mapToResponseBookingDto)
            .toList();

    }

    @Override
    public List<ResponseBookingDto> findBookingsForItemsByUserId(Long userId, String state) {

        LocalDateTime dateTime = LocalDateTime.now();

        boolean findUser = userRepositoryJpa.existsById(userId);
        if (!findUser) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL"      -> bookingRepositoryJpa.findBookingsForItemsByUserId(userId);
            case "CURRENT"  -> bookingRepositoryJpa.findCurrentBookingsForItemsByUserId(userId, dateTime);
            case "PAST"     -> bookingRepositoryJpa.findPastBookingsForItemsByUserId(userId, dateTime);
            case "FUTURE"   -> bookingRepositoryJpa.findFutureBookingsForItemsByUserId(userId, dateTime);
            case "WAITING"  -> bookingRepositoryJpa.findBookingsForItemsByUserIdAndStatus(
                userId, BookingStatus.WAITING
            );
            case "REJECTED" -> bookingRepositoryJpa.findBookingsForItemsByUserIdAndStatus(
                userId, BookingStatus.REJECTED
            );
            default         -> throw new ValidationException("Неизвестное состояние: " + state);
        };

        return bookings.stream()
            .map(mapper::mapToResponseBookingDto)
            .toList();
    }

}