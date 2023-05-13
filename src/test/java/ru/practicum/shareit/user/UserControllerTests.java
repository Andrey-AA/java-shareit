package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void saveUserTest() throws Exception {
        UserDto userDto = new UserDto(1L, "name","email@mail.ru","666");
        Mockito.when(userService.saveUser(any())).thenReturn(userDto);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))).andExpect(status().isOk());
    }

    @Test
    void getAllUsersTest() throws Exception {
        UserDto userDto = new UserDto(1L, "name","email@mail.ru","666");
        UserDto userDto2 = new UserDto(2L, "name2","email2@mail.ru","667");
        UserDto userDto3 = new UserDto(3L, "name3","email3@mail.ru","668");
        List<UserDto> listUsers = List.of(userDto, userDto2, userDto3);

        Mockito.when(userService.getAllUsers()).thenReturn(listUsers);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("email@mail.ru"));
    }

    @Test
    void findUserByIdTest() throws Exception {
        UserDto userDto = new UserDto(1L, "name","email@mail.ru","666");
        Mockito.when(userService.getById(any())).thenReturn(userDto);
        mockMvc.perform(get("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"));
    }

    @Test
    void removeUserTest() throws Exception {
        UserDto userDto = new UserDto(1L, "name","email@mail.ru","666");
        Mockito.when(userService.removeUser(1L)).thenReturn(userDto);
        mockMvc.perform(delete("/users/{id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void updateUserTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        UserDto updatedUser = new UserDto(1L, "updatedName","updatedemail@mail.ru", "666");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userService.updateUser(updatedUser, 1L)).thenReturn(updatedUser);
        mockMvc.perform(patch("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updatedemail@mail.ru"))
                .andExpect(jsonPath("$.name").value("updatedName"));
    }
}