package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;


    @PostMapping
    public BookingShort createBooking(@Valid @RequestBody BookingFromDomain bookingFromDomain,
                                      @RequestHeader(USER_ID) Long bookerId) {
        return bookingService.createBooking(bookingFromDomain, bookerId);
    }

    @GetMapping("/{id}")
    public BookingShort findBookingById(@PathVariable(value = "id") Long bookingId, @RequestHeader(USER_ID) Long requesterId) {
        return bookingService.findBookingById(bookingId, requesterId);
    }

    @PatchMapping("/{bookingId}")
    public BookingShort approveBooking(@PathVariable Long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader(USER_ID) Long requesterId) {
        return bookingService.approveBooking(bookingId, approved, requesterId);
    }

    @PatchMapping("/cancel/{bookingId}")
    public BookingShort cancelBooking(@PathVariable Long bookingId,
                                      @RequestParam Boolean canceled,
                                      @RequestHeader(USER_ID) Long requesterId) {
        return bookingService.cancelBooking(bookingId, requesterId);
    }

    @GetMapping()
    public List<BookingShort> findBookingsByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingState.checkState(state);
        return bookingService.findBookingsByUser(state, requesterId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingShort> findBookingsByOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader(USER_ID) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
       BookingState.checkState(state);
       return bookingService.findBookingsByOwner(state, requesterId, from, size);
    }
}
