package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingLong;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemJsonTests {
    @Autowired
    private JacksonTester<ItemLong> jsonLong;

    @Autowired
    private JacksonTester<ItemDto> jsonDto;

    @Test
    void testItemLong() throws Exception {

        ItemLong itemLong = ItemLong
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        JsonContent<ItemLong> result = jsonLong.write(itemLong);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
    }

    @Test
    void testItemDto() throws Exception {

        ItemDto itemDto = ItemDto
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        JsonContent<ItemDto> result = jsonDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
    }
}
