package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class RequestJsonTests {
    @Autowired
    private JacksonTester<UserDto> jsonFull;
    @Autowired
    private JacksonTester<ItemRequestDto> jsonDto;

    @Test
    void testItemRequestFull() throws Exception {

        UserDto itemRequestFull = UserDto
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();

        JsonContent<UserDto> result = jsonFull.write(itemRequestFull);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(1);
    }

    @Test
    void testItemRequestDto() throws Exception {

        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();

        JsonContent<ItemRequestDto> result = jsonDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(1);
    }
}
