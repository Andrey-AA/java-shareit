package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingLong;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingJsonTests {
    @Autowired
    private JacksonTester<BookingShort> jsonShort;

    @Autowired
    private JacksonTester<BookingLong> jsonLong;

    @Test
    void testBookingShortDto() throws Exception {

        BookingShort bookingShort = BookingShort
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 12, 10, 10, 1))
                .end(LocalDateTime.of(2023, 12, 20, 10, 10, 1))
                .build();

        JsonContent<BookingShort> result = jsonShort.write(bookingShort);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2023, 12, 12, 10, 10, 1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2023, 12, 20, 10, 10, 1).toString());
    }

    @Test
    void testBookingLongDto() throws Exception {

        BookingLong bookingLong = BookingLong
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 12, 10, 10, 1))
                .end(LocalDateTime.of(2023, 12, 20, 10, 10, 1))
                .build();

        JsonContent<BookingLong> result = jsonLong.write(bookingLong);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2023, 12, 12, 10, 10, 1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2023, 12, 20, 10, 10, 1).toString());
    }
}
