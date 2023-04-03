package ru.practicum.shareit.exception;

public class IncorrectBookingStatusException extends RuntimeException {
    public IncorrectBookingStatusException(String message) {
        super(message);
    }
}
