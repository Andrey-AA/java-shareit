package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private final String header = "X-Sharer-User-Id";

    @Test
    @Rollback
    void createItemTest() throws Exception {
        User user = userRepository.save(new User(1L, "name", "email@mail.ru"));
        Item item = new Item(1L, "item", "description", true, user.getId(), user.getId());
        item = itemRepository.save(item);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, user.getId())
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void findItemById() throws Exception {
        User user = userRepository.save(new User(1L, "name", "email@mail.com"));
        userRepository.save(new User(3L, "name2", "email2@mail.com"));
        Item item = new Item(1L, "item", "description", true, user.getId(), null);

        item = itemRepository.save(item);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, user.getId())
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void findItemsByUser() throws Exception {
        User user = userRepository.save(new User(1L, "name", "email@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "item", "description", true, user.getId(), user.getId()));
        Item item2 = itemRepository.save(new Item(2L, "item2", "description2", true, user.getId(), user.getId()));
        Item item3 = itemRepository.save(new Item(3L, "item3", "description3", true, user.getId(), user.getId()));
        List<Item> listItems = List.of(item, item2, item3);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, user.getId())
                        .content(objectMapper.writeValueAsString(listItems))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    @Rollback
    void updateItemTest() throws Exception {
        User user = userRepository.save(new User(1L, "name", "email@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "itemName", "description", true,
                user.getId(), user.getId()));
        Item updatedItem = new Item(item.getId(), "updatedItem", "updatedDescription",
                true, user.getId(), user.getId());

        mockMvc.perform(patch("/items/{itemId}", item.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header(header, user.getId())
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(jsonPath("$.name").value("updatedItem"))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void searchTest() throws Exception {
        User user = userRepository.save(new User(1L, "name", "email@mail.ru"));
        User user2 = userRepository.save(new User(2L, "name", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "text", "text", true, user.getId(), user2.getId()));
        Item item2 = itemRepository.save(new Item(2L, "itemName2", "description2", true, user.getId(), user.getId()));
        Item item3 = itemRepository.save(new Item(3L, "itemName3", "description3", true, user.getId(), user.getId()));
        List<Item> listItems = List.of(item);
        List<Item> result = itemRepository.search("text");

        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(result.size())));
    }

    @Test
    @Rollback
    void saveComment() throws Exception {
        User owner = userRepository.save(new User(1L, "name", "email@mail.ru"));
        User booker = userRepository.save(new User(2L, "name2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "text", "text", true, owner.getId(), booker.getId()));
        CommentDto commentDto = new CommentDto(1L, "text", owner.getId(), "name", booker.getId(),
                LocalDateTime.now());
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(3), item, booker, BookingStatus.APPROVED));

        mockMvc.perform(post("/items/{itemId}/comment", item.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header(header, booker.getId())
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("text"));
    }

    @Test
    @Rollback
    void saveEmptyComment() {
        User owner = userRepository.save(new User(1L, "name", "email@mail.ru"));
        User booker = userRepository.save(new User(2L, "name2", "email2@mail.ru"));
        Item item = itemRepository.save(new Item(1L, "text", "text", true, owner.getId(), booker.getId()));
        CommentDto commentDto = new CommentDto(1L, "", owner.getId(), "name", 5000000L,
                LocalDateTime.now());
        bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(3), item, booker, BookingStatus.APPROVED));

        assertThrows(InvalidItemParametersException.class, () -> itemService.saveComment(commentDto, booker.getId(), item.getId()));
        assertThrows(InvalidItemParametersException.class, () -> itemService.saveComment(commentDto, 5000000L, item.getId()));
    }

    @Test
    @Rollback
    void checkItemParametersTest() {
        final ItemDto itemDto1 = ItemDto
                .builder()
                .id(1L)
                .name("itemName")
                .description("")
                .available(true)
                .build();
        assertThrows(InvalidItemParametersException.class, () -> itemService.saveItem(itemDto1, 1L));
        final ItemDto itemDto2 = ItemDto
                .builder()
                .id(2L)
                .name("itemName")
                .description("itemDesc")
                .available(null)
                .build();
        assertThrows(InvalidItemParametersException.class, () -> itemService.saveItem(itemDto2, 1L));
    }

    @Test
    void checkUpdateItem_withException() {
        final ItemDto itemDto = ItemDto
                .builder()
                .id(1L)
                .name("itemName")
                .description("")
                .available(true)
                .build();
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 1L));

    }

    @Test
    void checkUpdateItem_withOutException_andCheckIf() {
        UserDto user = new UserDto(1, "testUser", "testEmail@test.com", "testDto");
        user = userService.saveUser(user);

        ItemDto itemDto = ItemDto
                .builder()
                .name("itemName")
                .description("description")
                .available(true)
                .owner(user.getId())
                .build();
        itemDto = itemService.saveItem(itemDto, user.getId());

        final ItemDto newItemDto = ItemDto
                .builder()
                .id(itemDto.getId())
                .name(null)
                .description(null)
                .available(null)
                .owner(null)
                .build();

        assertEquals(itemDto, itemService.updateItem(newItemDto, user.getId(), itemDto.getId()));
    }

    @Test
    void equalsAndHashCodeTest() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment1 = new Comment(1L, "testComment", 1L, 1L, now);
        Comment comment2 = new Comment(1L, "testComment", 1L, 1L, now);
        Comment comment3 = new Comment(2L, "testComment", 1L, 1L, now);

        assertTrue(comment1.equals(comment2) && comment2.equals(comment1));
        assertEquals(comment1.hashCode(), comment2.hashCode());

        assertFalse(comment1.equals(comment3) || comment3.equals(comment1));
        assertNotEquals(comment1.hashCode(), comment3.hashCode());

        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());

        assertNotEquals("test", comment1);
        assertNotEquals(comment1.hashCode(), "test".hashCode());

        assertEquals(comment1, comment1);
        assertEquals(comment1.hashCode(), comment1.hashCode());
    }

    @Test
    void testToDTOs() {
        Item item1 = new Item(1L, "Item 1", "Description 1", true, 1L, 1L);
        Item item2 = new Item(2L, "Item 2", "Description 2", false, 2L, 2L);

        List<ItemDto> dtos = ItemMapper.toDTOs(Arrays.asList(item1, item2));

        assertEquals(2, dtos.size());
        assertEquals(item1.getId(), dtos.get(0).getId());
        assertEquals(item2.getName(), dtos.get(1).getName());
    }

    @Test
    void testToItemLong() {
        Item item = new Item(1L, "Item 1", "Description 1", true, 1L, 1L);
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(2), item, new User(1L, "User 1", "email1@mail.ru"), BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, new User(2L, "User 2", "email2@mail.ru"), BookingStatus.APPROVED);
        CommentDto comment = new CommentDto(1L, "Comment 1", 1L, "User 1", 1L, LocalDateTime.now());

        ItemLong itemLong = ItemMapper.toItemLong(item, lastBooking, nextBooking, List.of(comment));

        assertEquals(item.getId(), itemLong.getId());
        assertEquals(lastBooking.getId(), itemLong.getLastBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), itemLong.getNextBooking().getBookerId());
        assertEquals(comment.getText(), itemLong.getComments().get(0).getText());
    }

    @Test
    void testToLong() {
        Item item = new Item(1L, "Item 1", "Description 1", true, 1L, 1L);
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(2), item, new User(1L, "User 1", "email1@mail.ru"), BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, new User(2L, "User 2", "email2@mail.ru"), BookingStatus.APPROVED);
        CommentDto comment = new CommentDto(1L, "Comment 1", 1L, "User 1", 1L, LocalDateTime.now());

        ItemLong itemLong = ItemMapper.toLong(item, Arrays.asList(lastBooking, nextBooking), List.of(comment));

        assertEquals(item.getId(), itemLong.getId());
        assertEquals(lastBooking.getId(), itemLong.getLastBooking().getId());
        assertEquals(nextBooking.getBooker().getId(), itemLong.getNextBooking().getBookerId());
        assertEquals(comment.getText(), itemLong.getComments().get(0).getText());
    }
}
