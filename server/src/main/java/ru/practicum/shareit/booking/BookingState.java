package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState checkState(String state) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}
