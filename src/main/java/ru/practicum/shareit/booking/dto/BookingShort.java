package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class BookingShort {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private Item item;
    private BookingStatus status;

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }
}

