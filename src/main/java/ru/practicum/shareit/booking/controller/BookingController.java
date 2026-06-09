package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Validated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookingDto bookingItemRequest(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long bookerId,
        @RequestBody RequestBookingDto requestDto
    ) {
        return bookingService.save(requestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBookingDto bookingApproved(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "approved") Boolean approved, @PathVariable
        @Positive Long bookingId
    ) {
        return bookingService.approvedBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBookingDto findBookingById(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @PathVariable @Positive Long bookingId
    ) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findBookingsByUserId(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "state", defaultValue = "ALL", required = false) String state
    ) {
        return bookingService.findAllBookingsUserById(state, userId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findBookingsByOwnerId(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "state", defaultValue = "ALL") String state
    ) {
        return bookingService.findBookingsForItemsByUserId(userId,state);
    }

}
