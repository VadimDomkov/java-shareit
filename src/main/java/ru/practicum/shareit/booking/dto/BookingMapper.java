package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking dtoToBooking(BookingDto bookingDto);

    BookingDto bookingToDto(Booking request);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingForItemDto bookingToBookingItemDto(Booking booking);
}
