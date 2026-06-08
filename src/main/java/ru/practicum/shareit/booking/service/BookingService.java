package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

@Service
public interface BookingService {

    ResponseBookingDto save(RequestBookingDto dto, Long bookerId);

    ResponseBookingDto approvedBooking(Long userId, Boolean approved, Long bookingId);

    ResponseBookingDto findBookingById(Long userId, Long bookingId);

    List<ResponseBookingDto> findAllBookingsUserById(String status, Long userId);

    List<ResponseBookingDto> findBookingsForItemsByUserId(Long userId, String state);

}
