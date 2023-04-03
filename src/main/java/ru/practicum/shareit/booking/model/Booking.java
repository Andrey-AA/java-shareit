package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Long item;
    private Long booker;
    private BookingStatus status;
}
