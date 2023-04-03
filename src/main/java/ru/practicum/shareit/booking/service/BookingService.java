package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {

    List<BookingDto> getAllBookings();

    BookingDto findBookingById(Long id);

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto, Long userId, Long bookingId);
}
