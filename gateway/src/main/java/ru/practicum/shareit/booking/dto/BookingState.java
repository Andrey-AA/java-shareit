package ru.practicum.shareit.booking.dto;

public enum BookingState {
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static BookingState checkState(String state) {
		for (BookingState value : BookingState.values()) {
			if (value.name().equalsIgnoreCase(state)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown state: " + state);
	}
}
