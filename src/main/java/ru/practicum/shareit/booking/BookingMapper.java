package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking dtoToBooking(BookingDto bookingDto);

    BookingDto bookingToDto(Booking request);
}
