package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Test
    @Rollback
    void saveUserTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        userRepository.save(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    @Rollback
    void getAllUsersTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        User user2 = new User(2L, "name2","email2@mail.ru");
        User user3 = new User(3L, "name3","email3@mail.ru");
        userRepository.save(user);
        userRepository.findAll();
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("email@mail.ru"));
    }

    @Test
    @Rollback
    void findUserByIdTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        userRepository.save(user);
        userRepository.getReferenceById(user.getId());

        mockMvc.perform(get("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"));
    }

    @Test
    @Rollback
    void removeUserTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        userRepository.save(user);

        mockMvc.perform(delete("/users/{id}",user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    @Rollback
    void updateUserTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        User updatedUser = new User(1L, "updatedName","updatedemail@mail.ru");
        userRepository.save(user);
        userRepository.save(updatedUser);

        mockMvc.perform(patch("/users/{id}", updatedUser.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updatedemail@mail.ru"))
                .andExpect(jsonPath("$.name").value("updatedName"));
    }
}
