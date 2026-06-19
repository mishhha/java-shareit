package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookingDto bookingItemRequest(
        @RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
        @RequestBody RequestBookingDto requestDto
    ) {
        return bookingService.save(requestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto bookingApproved(
        @RequestHeader(value = "X-Sharer-User-Id") Long userId,
        @RequestParam(value = "approved") Boolean approved,
        @PathVariable Long bookingId
    ) {
        return bookingService.approvedBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findBookingById(
        @RequestHeader(value = "X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId
    ) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> findBookingsByUserId(
        @RequestHeader(value = "X-Sharer-User-Id") Long userId,
        @RequestParam(value = "state", defaultValue = "ALL", required = false) String state
    ) {
        return bookingService.findAllBookingsUserById(state, userId);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> findBookingsByOwnerId(
        @RequestHeader(value = "X-Sharer-User-Id") Long userId,
        @RequestParam(value = "state", defaultValue = "ALL") String state
    ) {
        return bookingService.findBookingsForItemsByUserId(userId,state);
    }

}