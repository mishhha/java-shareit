package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;

import java.util.List;

public interface BookingService {

    ResponseBookingDto save(RequestBookingDto dto, Long bookerId);

    ResponseBookingDto approvedBooking(Long userId, Boolean approved, Long bookingId);

    ResponseBookingDto findBookingById(Long userId, Long bookingId);

    List<ResponseBookingDto> findAllBookingsUserById(String status, Long userId);

    List<ResponseBookingDto> findBookingsForItemsByUserId(Long userId, String state);

}
