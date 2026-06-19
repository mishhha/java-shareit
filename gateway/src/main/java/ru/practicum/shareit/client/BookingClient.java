package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.booking.RequestBookingDto;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${server.base-url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createBooking(Long userId, RequestBookingDto dto) {
        return post("/bookings", userId, dto);
    }

    public ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        String path = "/bookings/" + bookingId + "?approved=" + approved;
        return patch(path, userId, (Map<String, Object>) null, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookings(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/bookings", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/bookings/owner", userId, parameters);
    }
}