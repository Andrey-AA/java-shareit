package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
public class BookingShort {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
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

