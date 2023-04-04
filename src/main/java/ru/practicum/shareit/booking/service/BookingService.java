package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.List;

public interface BookingService {
    List<BookingShort> getAllBookings();

    BookingShort createBooking(BookingFromDomain bookingFromDomain, Long bookerId);

    BookingShort findBookingById(Long bookingId, Long requesterId);

    BookingShort approveBooking(Long bookingId, boolean approved, Long requesterId);

    BookingShort cancelBooking(Long bookingId, boolean canceled, Long requesterId);

    List<BookingShort> findBookingsByUser(String state, Long requesterId);

    List<BookingShort> findBookingsByOwner(String state, Long requesterId);
}
