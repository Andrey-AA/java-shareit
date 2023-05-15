package ru.practicum.shareit.booking.dto;

import lombok.NonNull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking toBooking(@NonNull BookingFromDomain bookingFromDomain, Item item, User user) {
        return new Booking(
                bookingFromDomain.getId(),
                bookingFromDomain.getStart(),
                bookingFromDomain.getEnd(),
                item,
                user,
                bookingFromDomain.getStatus()
        );
    }

    public static BookingShort toBookingShort(@NonNull Booking booking) {
        return new BookingShort(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingShort.Booker(
                        booking.getBooker().getId(),
                        booking.getBooker().getName()
                ),
                new BookingShort.Item(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                booking.getStatus()
        );
    }

    public static List<BookingShort> toBookingShorts(List<Booking> bookings) {
        ArrayList<BookingShort> bookingShorts = new ArrayList<>();

        for (Booking booking: bookings) {
            bookingShorts.add(toBookingShort(booking));
        }
        return bookingShorts;
    }
}
