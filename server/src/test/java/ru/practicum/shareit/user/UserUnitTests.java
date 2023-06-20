package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserUnitTests {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void removeUser() {
        UserDto user = new UserDto(1L, "updatedName", "updatedemail@mail.ru", "666");
        user = userService.saveUser(user);
        assertEquals(1, userService.getAllUsers().size());

        userService.removeUser(user.getId());
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    @Transactional
    void updateUserTest() throws Exception {
        UserDto user = new UserDto(1L, "Name", "email@mail.ru", "666");
        UserDto updatedUser = new UserDto(1L, "updatedName","updatedemail@mail.ru", "666");
        user = userService.saveUser(user);
        userService.updateUser(updatedUser,user.getId());

        assertEquals(updatedUser.getName(), userService.getById(user.getId()).getName());
        assertEquals(user.getId(), userService.getById(user.getId()).getId());
    }

    @Test
    @Rollback
    @Transactional
    void saveUserTest() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");
        userService.saveUser(user);
        assertThat(user).isNotNull();
    }
}
