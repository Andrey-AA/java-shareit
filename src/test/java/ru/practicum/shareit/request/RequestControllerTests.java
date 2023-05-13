package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFull;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    void createItemRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"description", 1L,LocalDateTime.now());

        Mockito.when(itemRequestService.saveItemRequest(1L, itemRequestDto)).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests",itemRequestDto)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto))).andExpect(status().isOk());
    }

    @Test
    void getItemRequestByUserIdTest() throws Exception {
        ItemRequestFull itemRequestFull = new ItemRequestFull(1L,"description",1L,LocalDateTime.now(),null);
        ItemRequestFull itemRequestFull2 = new ItemRequestFull(2L,"description2",1L,LocalDateTime.now(),null);
        ItemRequestFull itemRequestFull3 = new ItemRequestFull(3L,"description3",1L,LocalDateTime.now(),null);
        List<ItemRequestFull> itemRequests = List.of(itemRequestFull, itemRequestFull2, itemRequestFull3);

        Mockito.when(itemRequestService.getItemRequestsByUserId(1L)).thenReturn(itemRequests);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        ItemRequestFull itemRequestFull = new ItemRequestFull(1L,"description",1L,LocalDateTime.now(),null);
        ItemRequestFull itemRequestFull2 = new ItemRequestFull(2L,"description2",1L,LocalDateTime.now(),null);
        ItemRequestFull itemRequestFull3 = new ItemRequestFull(3L,"description3",1L,LocalDateTime.now(),null);
        List<ItemRequestFull> itemRequests = List.of(itemRequestFull, itemRequestFull2, itemRequestFull3);

        Mockito.when(itemRequestService.getItemRequestsByUserId(1L)).thenReturn(itemRequests);
        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        ItemRequestFull itemRequestFull = new ItemRequestFull(1L,"description",1L,LocalDateTime.now(),null);

        Mockito.when(itemRequestService.getItemRequestById(1L,1L)).thenReturn(itemRequestFull);
        mockMvc.perform(get("/requests/{requestId}", 1L).header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestFull))).andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.requesterId").value(1L));
    }
}
