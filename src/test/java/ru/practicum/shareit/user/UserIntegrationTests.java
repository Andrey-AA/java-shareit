package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @Autowired
    private UserService userService;

    @Test
    @Rollback
    void givenUserWhenGetUsersThenReturnJsonArray() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");

        List<UserDto> allUsers = Arrays.asList(user);

        given(userServiceMock.getAllUsers()).willReturn(allUsers);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())));
    }

    @Test
    @Rollback
    void givenUserWhenGetUserByIdThenReturnJson() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");

        given(userServiceMock.getById(1L)).willReturn(user);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    @Rollback
    void givenUserWhenSaveUserThenStatus201() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");

        given(userServiceMock.saveUser(user)).willReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void givenUserWhenDeleteUserThenStatus200() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");

        given(userServiceMock.removeUser(1L)).willReturn(user);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void givenUserWhenUpdateUserThenStatus200() throws Exception {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");

        given(userServiceMock.updateUser(user, 1L)).willReturn(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1L, "Test", "Test@example.com");
        User user2 = new User(1L, "Test", "Test@example.com");

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeDifferentIds() {
        User user1 = new User(1L, "Test", "Test@example.com");
        User user2 = new User(2L, "Test", "Test@example.com");

        assertThat(user1).isNotEqualTo(user2);
        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }
}

