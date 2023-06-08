package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@WebMvcTest(controllers = BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    private ItemDto itemDto;

    private BookingShort bookingDto;

    private BookingFromDomain bookingShort;

    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void init() {
        userDto = UserDto
                .builder()
                .id(2L)
                .name("userName")
                .email("user@email.ru")
                .build();

        itemDto = ItemDto
                .builder()
                .id(1L)
                .name("itemName")
                .description("description")
                .available(true)
                .build();

        BookingShort.Booker booker = new BookingShort.Booker(userDto.getId(), userDto.getName());
        BookingShort.Item bookingItem = new BookingShort.Item(itemDto.getId(), itemDto.getName());
        bookingDto = BookingShort
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 12, 10, 0))
                .end(LocalDateTime.of(2023, 12, 20, 10, 0))
                .booker(booker)
                .item(bookingItem)
                .build();

        bookingShort = BookingFromDomain.builder()
                .start(LocalDateTime.of(2023, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(1L).build();
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(bookingService.createBooking(bookingShort, 1L)).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .header(header, 1)
                        .content(objectMapper.writeValueAsString(bookingShort))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void approveTest() throws Exception {
        bookingDto.setStatus(APPROVED);
        Mockito.when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        Mockito.when(bookingService.findBookingsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByUserTest() throws Exception {
        Mockito.when(bookingService.findBookingsByUser(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getByIdTest() throws Exception {
        Mockito.when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllByUserWrongStateTest() throws Exception {
        Mockito.when(bookingService.findBookingsByUser(any(), anyLong(),  anyInt(), anyInt()))
                .thenThrow(InvalidItemParametersException.class);
        mockMvc.perform(get("/bookings?from=0?size=20?state=FUTURE2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
