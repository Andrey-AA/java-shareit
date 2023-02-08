package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto) {
        return bookingServiceImpl.createBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable(value = "bookingId") Long bookingId) {
        return bookingServiceImpl.updateBooking(bookingDto, userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getAllItemRequests() {
        return bookingServiceImpl.getAllBookings();
    }

    @GetMapping("/{id}")
    public BookingDto findBookingById(@PathVariable(value = "id") Long id) {
        return bookingServiceImpl.findBookingById(id);
    }
}
