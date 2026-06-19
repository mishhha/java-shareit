package ru.practicum.shareit.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.booking.RequestBookingDto;
import ru.practicum.shareit.dto.booking.ResponseBookingDto;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.dto.item.ItemResponseDto;
import ru.practicum.shareit.dto.user.UserResponseDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public Booking mapToBooking(RequestBookingDto dto) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    public ResponseBookingDto mapToResponseBookingDto(Booking booking) {

        if (booking == null) {
            return null;
        }

        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(booking.getId());

        if (booking.getItem() != null) {
            ItemResponseDto itemDto = itemMapper.mapToItemDto(booking.getItem());
            dto.setItem(itemDto);
        }

        if (booking.getBooker() != null) {
            UserResponseDto userDto = userMapper.mapToUserDto(booking.getBooker());
            dto.setBooker(userDto);
        }

        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        return dto;
    }

}