package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingIntegrationTests {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto userDto1;

    // private BookingFromDomain bookingShortDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto1 = UserDto.builder()
                .name("name")
                .email("user1@email.com")
                .build();

    }

    private static BookingFromDomain createBookingFromDomain(long itemId) {
        return BookingFromDomain.builder()
                .start(LocalDateTime.of(2023, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(itemId).build();
    }

    @Test
    void getAllBookingsTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto booker = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), booker.getId());
    }

    @Test
    void createTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertEquals(1L, bookingController.findBookingById(booking.getId(), user1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingController.createBooking(createBookingFromDomain(1L), 1L));
    }

    @Test
    void createForWrongItemTest() {
        UserDto user = userController.saveUser(userDto);
        assertThrows(EntityNotFoundException.class, () -> bookingController.createBooking(createBookingFromDomain(1L), 1L));
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        assertThrows(EntityNotFoundException.class, () -> bookingController.createBooking(createBookingFromDomain(item.getId()), 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.saveUser(userDto);
        itemDto.setAvailable(false);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        assertThrows(InvalidItemParametersException.class, () -> bookingController.createBooking(createBookingFromDomain(item.getId()), 2L));
    }

    @Test
    void createWithWrongEndDate() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingFromDomain bookingShortDto = createBookingFromDomain(item.getId());
        bookingShortDto.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(ItemNotAvailableException.class, () -> bookingController.createBooking(bookingShortDto, user1.getId()));
    }

    @Test
    void approveTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertEquals(WAITING, bookingController.findBookingById(booking.getId(), user1.getId()).getStatus());
        bookingController.approveBooking(booking.getId(), true, user.getId());
        assertEquals(APPROVED, bookingController.findBookingById(booking.getId(), user1.getId()).getStatus());
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingController.approveBooking(1L, true, 1L));
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertThrows(IncorrectParameterException.class, () -> bookingController.approveBooking(1L, true, 2L));
    }

    @Test
    void approveBookingRejectTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertEquals(WAITING, bookingController.findBookingById(booking.getId(), user1.getId()).getStatus());
        bookingController.approveBooking(booking.getId(), false, user.getId());
        assertEquals(REJECTED, bookingController.findBookingById(booking.getId(), user1.getId()).getStatus());
    }

    @Test
    void approveToBookingWithWrongStatus() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        bookingController.approveBooking(1L, true, 1L);
        assertThrows(InvalidItemParametersException.class, () -> bookingController.approveBooking(1L, true, 1L));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertEquals(1, bookingController.findBookingsByUser("WAITING", user1.getId(), 0, 10).size());
        assertEquals(1, bookingController.findBookingsByUser("ALL", user1.getId(), 0, 10).size());
        assertEquals(0, bookingController.findBookingsByUser("PAST", user1.getId(),  0, 10).size());
        assertEquals(0, bookingController.findBookingsByUser("CURRENT", user1.getId(), 0, 10).size());
        assertEquals(1, bookingController.findBookingsByUser("FUTURE", user1.getId(),  0, 10).size());
        assertEquals(0, bookingController.findBookingsByUser("REJECTED", user1.getId(),  0, 10).size());
        bookingController.approveBooking(booking.getId(), true, user.getId());
        assertEquals(0, bookingController.findBookingsByOwner("CURRENT", user.getId(), 0, 10).size());
        assertEquals(1, bookingController.findBookingsByOwner("ALL", user.getId(), 0, 10).size());
        assertEquals(0, bookingController.findBookingsByOwner("WAITING", user.getId(),  0, 10).size());
        assertEquals(1, bookingController.findBookingsByOwner("FUTURE", user.getId(),  0, 10).size());
        assertEquals(0, bookingController.findBookingsByOwner("REJECTED", user.getId(),  0, 10).size());
        assertEquals(0, bookingController.findBookingsByOwner("PAST", user.getId(),  0, 10).size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingController.findBookingsByUser("ALL",1L, 0, 10));
        assertThrows(EntityNotFoundException.class, () -> bookingController.findBookingsByOwner("ALL", 1L, 0, 10));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(EntityNotFoundException.class, () -> bookingController.findBookingById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto user1 = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), user1.getId());
        assertThrows(IncorrectParameterException.class, () -> bookingController.findBookingById(1L, 10L));
    }

    @Test
    void cancelBookingTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto booker = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), booker.getId());
        bookingController.approveBooking(booking.getId(), true, user.getId());
        assertEquals(CANCELED, bookingController.cancelBooking(booking.getId(), true, booker.getId()).getStatus());
    }

    @Test
    void cancelBookingWrongBookingTest() {
        UserDto user = userController.saveUser(userDto);
        ItemDto item = itemController.createItem(itemDto, user.getId());
        UserDto booker = userController.saveUser(userDto1);
        BookingShort booking = bookingController.createBooking(createBookingFromDomain(item.getId()), booker.getId());
        bookingController.approveBooking(booking.getId(), true, user.getId());
        assertThrows(EntityNotFoundException.class,
                () -> bookingController.cancelBooking(1000L, true, booker.getId()));
    }
}
