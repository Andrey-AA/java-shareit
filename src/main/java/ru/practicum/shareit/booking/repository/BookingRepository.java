package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository {
    List<Booking> getAllBookings();

    Booking findBookingById(Long id);

    Booking createBooking(Booking booking);

    Booking updateBooking(Booking booking, Long userId, Long bookingId);
}
