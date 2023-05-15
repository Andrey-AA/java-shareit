package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    @MockBean
    ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";


    private Item initItem;

    private ItemDto initItemDto;

    @BeforeEach
    void init() {

        Long ownerId = 1L;
        Long itemId = 1L;
        initItemDto = ItemDto.builder()
                .id(itemId)
                .name("Item")
                .description("Item Description")
                .available(true)
                .owner(ownerId)
                .requestId(1L)
                .build();

        initItem = Item.builder()
                .id(itemId)
                .name("Item")
                .description("Item Description")
                .available(true)
                .owner(ownerId)
                .requestId(1L)
                .build();
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L, 1L);
        Mockito.when(itemService.saveItem(itemDto, 1L)).thenReturn(itemDto);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(status().isOk());
    }

    @Test
    void findItemById() throws Exception {
        ItemLong itemLong = new ItemLong(1L,"itemName","itemDescription",true,1L,1L,null,null,null);
        Mockito.when(itemService.getItemById(1L, 1L)).thenReturn(itemLong);

        mockMvc.perform(get("/items/{itemId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemLong)))
                .andExpect(jsonPath("$.name").value("itemName"))
                .andExpect(status().isOk());
    }

    @Test
    void findItemsByUser() throws Exception {
        ItemLong itemLong = new ItemLong(1L,"itemName","itemDescription",true,1L,1L,null,null,null);
        ItemLong itemLong2 = new ItemLong(2L,"itemName2","itemDescription2",true,2L,2L,null,null,null);
        ItemLong itemLong3 = new ItemLong(3L,"itemName3","itemDescription3",true,3L,3L,null,null,null);
        List<ItemLong> listItems = List.of(itemLong, itemLong2, itemLong3);
        Mockito.when(itemService.findItemsByUser(1L)).thenReturn(listItems);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(listItems))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("itemName"));
    }

    @Test
    void updateItemTest() throws Exception {
        Item item = new Item(1L,"itemName","description",true,1L,1L);
        ItemDto updatedItem = new ItemDto(1L,"updatedItem","updatedDescription",true,1L,1L);
        Mockito.when(itemRepository.save(item)).thenReturn(item);
        Mockito.when(itemService.updateItem(updatedItem,1L,1L)).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(jsonPath("$.name").value("updatedItem"))
                .andExpect(status().isOk());
    }

    @Test
    void searchTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L,"name","text",true,1L,1L);
        ItemDto itemDto2 = new ItemDto(2L,"name2","description2",true,1L,1L);
        ItemDto itemDto3 = new ItemDto(3L,"name3","description3",true,1L,1L);
        List<ItemDto> listItems = List.of(itemDto, itemDto2, itemDto3);

        Mockito.when(itemService.search(anyString())).thenReturn(listItems);
        mockMvc.perform(get("/items/search")
                        .param("text", anyString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listItems.size())))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void saveComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "text", 1L, "name", 1L, LocalDateTime.now());
        Mockito.when(itemService.saveComment(commentDto, 1L, 1L)).thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", 1L).contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("text"));
    }

}


