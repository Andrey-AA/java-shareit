package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestService itemRequestService;

    @Test
    @Rollback
    void createItemRequestTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L,"description",1L,LocalDateTime.now().minusDays(5));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"description",1L,LocalDateTime.now().minusDays(5));
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        mockMvc.perform(post("/requests",itemRequest)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    @Rollback
    void createRequestWithoutDescriptionTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"",1L,LocalDateTime.now().minusDays(5));
        userRepository.save(user);
        assertThrows(InvalidItemParametersException.class, () -> itemRequestService.saveItemRequest(1L,itemRequestDto));
    }

    @Test
    @Rollback
    void getItemRequestByUserIdTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L,"description",1L,LocalDateTime.now().minusDays(5));
        ItemRequest itemRequest2 = new ItemRequest(2L,"description2",1L,LocalDateTime.now().minusDays(5));
        ItemRequest itemRequest3 = new ItemRequest(3L,"description3",1L,LocalDateTime.now().minusDays(5));
        userRepository.save(user);
        itemRequestRepository.deleteAll();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);

        List<ItemRequest> itemRequests = List.of(itemRequest, itemRequest2, itemRequest3);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()", is(itemRequests.size())))
                        .andExpect(jsonPath("[0].description", is(itemRequest3.getDescription())));
    }

    @Test
    @Rollback
    void getAllItemRequestsTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L,"description",1L,LocalDateTime.now().minusDays(5));
        ItemRequest itemRequest2 = new ItemRequest(2L,"description2",1L,LocalDateTime.now().minusDays(5));
        ItemRequest itemRequest3 = new ItemRequest(3L,"description3",1L,LocalDateTime.now().minusDays(5));
        userRepository.save(user);
        itemRequestRepository.deleteAll();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);

        List<ItemRequest> itemRequests = List.of(itemRequest, itemRequest2, itemRequest3);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()", is(itemRequests.size())))
                        .andExpect(jsonPath("[0].description", is(itemRequest3.getDescription())));;
    }

    @Test
    @Rollback
    void getItemRequestByIdTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L,"description",1L,LocalDateTime.now().minusDays(5));
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        mockMvc.perform(get("/requests/{requestId}", 1L).header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))).andExpect(status().isOk())
                        .andExpect(jsonPath("$.description").value("description"))
                        .andExpect(jsonPath("$.requesterId").value(1L));
    }
}
