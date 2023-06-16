package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	private static final String USER_ID = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) Long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @Min(0) @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Min(1) @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState.checkState(stateParam);
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, stateParam, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID) Long userId,
												@RequestBody @Valid BookingFromDomain requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findBookingById(@RequestHeader(USER_ID) Long userId,
												  @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.findBookingById(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID) Long ownerId,
												@PathVariable Long bookingId,
												@RequestParam Boolean approved) {
		log.info("Обновление брони владельцем. Подтверждение или отклонение брони.");
		return bookingClient.updateBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findBookingsByOwner(
			@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
			@RequestHeader(USER_ID) Long requesterId,
			@RequestParam(name = "from", defaultValue = "0") Integer from,
			@RequestParam(name = "size", defaultValue = "20") Integer size) {
		BookingState.checkState(state);
		return bookingClient.findBookingsByOwner(requesterId, state, from, size);
	}
}