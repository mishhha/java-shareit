package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.client.BookingClient;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @Validated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> bookingItemRequest(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long bookerId,
        @Valid @RequestBody RequestBookingDto requestDto
    ) {
        return bookingClient.createBooking(bookerId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> bookingApproved(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "approved") Boolean approved, @PathVariable
        @Positive Long bookingId
    ) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findBookingById(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @PathVariable @Positive Long bookingId
    ) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findBookingsByUserId(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "state", defaultValue = "ALL", required = false) String state
    ) {
        return bookingClient.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findBookingsByOwnerId(
        @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
        @RequestParam(value = "state", defaultValue = "ALL") String state
    ) {
        return bookingClient.getAllBookingsByOwner(userId, state);
    }

}
