package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    public Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus()
        );
    }

    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public List<BookingDto> toDTOs(List<Booking> bookings) {
        ArrayList<BookingDto> bookingsDto = new ArrayList<>();

        for(Booking booking: bookings) {
            bookingsDto.add(toDto(booking));
        }
        return bookingsDto;
    }
}
