package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class UserJsonTests {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {

        UserDto userDto = UserDto
                .builder()
                .id(1L)
                .name("name")
                .email("email")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("email");
    }

    @Test
    void hashCodeTest() {
      User user1 = new User(1L,"name","user@mail.com");
      User user2 = new User(1L,"name","user@mail.com");
      User user3 = new User(1L,"name2","user2@mail.com");
        assertEquals(user1, user2);
        assertEquals(user1, user3);
    }
}
