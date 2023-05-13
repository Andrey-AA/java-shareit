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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    BookingRepository bookingRepository;

    private final String header = "X-Sharer-User-Id";

    @Test
    @Rollback
    void createItemTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        Item item = new Item(1L,"item","description",true,1L,1L);
        userRepository.save(user);
        itemRepository.save(item);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(item)))
                        .andExpect(jsonPath("$.name").value("item"))
                        .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void findItemById() throws Exception {
        User user = new User(1L, "name","email@mail.com");
        User user2 = new User(3L, "name2","email2@mail.com");
        Item item = new Item(1L,"item","description",true,1L,null);
        List<Comment> comments = new ArrayList<>();
        List<CommentDto> commentDtoList = comments.stream()
                .map(comment -> ItemMapper.toCommentDto(comment, userRepository))
                .collect(Collectors.toList());
        ItemLong itemLong = new ItemLong(1L,"item","description",true,1L,1L,null,null,commentDtoList);
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.findById(1L);

        mockMvc.perform(get("/items/{itemId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(item)))
                        .andExpect(jsonPath("$.name").value("item"))
                        .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void findItemsByUser() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        Item item = new Item(1L,"item","description",true,1L,1L);
        Item item2 = new Item(2L,"item2","description2",true,1L,1L);
        Item item3 = new Item(3L,"item3","description3",true,1L,1L);
        List<Item> listItems = List.of(item, item2, item3);
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.findAllByOwner(1L);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(listItems))).andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    @Rollback
    void updateItemTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        Item item = new Item(1L,"itemName","description",true,1L,1L);
        Item updatedItem = new Item(1L,"updatedItem","updatedDescription",true,1L,1L);
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(updatedItem);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(item)))
                        .andExpect(jsonPath("$.name").value("itemName"))
                        .andExpect(status().isOk());
    }

    @Test
    @Rollback
    void searchTest() throws Exception {
        User user = new User(1L, "name","email@mail.ru");
        Item item = new Item(1L,"text","text",true,1L,1L);
        Item item2 = new Item(2L,"itemName2","description2",true,1L,1L);
        Item item3 = new Item(3L,"itemName3","description3",true,1L,1L);
        List<Item> listItems = List.of(item);
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.search("text");

        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()", is(listItems.size())))
                        .andExpect(jsonPath("[0].name", is(item.getName())));
    }

    @Test
    @Rollback
    void saveComment() throws Exception {
        User owner = new User(1L, "name","email@mail.ru");
        User booker = new User(2L, "name2","email2@mail.ru");
        Item item = new Item(1L,"text","text",true,1L,1L);
        CommentDto commentDto = new CommentDto(1L,"text",1L,"name",2L, LocalDateTime.now());
        Booking booking = new Booking(1L,LocalDateTime.now().minusDays(7),LocalDateTime.now().minusDays(3),item,booker, BookingStatus.APPROVED);
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
        itemService.saveComment(commentDto,2L,1L);

        mockMvc.perform(post("/items/{itemId}/comment",1L).contentType(MediaType.APPLICATION_JSON)
                        .header(header, 2L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.text").value("text"));
    }
}
