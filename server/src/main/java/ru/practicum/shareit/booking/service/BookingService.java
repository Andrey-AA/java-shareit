package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.List;

public interface BookingService {
    List<BookingShort> getAllBookings();

    BookingShort createBooking(BookingFromDomain bookingFromDomain, Long bookerId);

    BookingShort findBookingById(Long bookingId, Long requesterId);

    BookingShort approveBooking(Long bookingId, boolean approved, Long requesterId);

    BookingShort cancelBooking(Long bookingId, Long requesterId);

    List<BookingShort> findBookingsByUser(String state, Long requesterId, Integer from, Integer size);

    List<BookingShort> findBookingsByOwner(Long requesterId, String state, Integer from, Integer size);
}
