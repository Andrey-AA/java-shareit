package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTests {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllItemsItemsTest() {
        List<Item> allItems = itemRepository.findAll();
        assertThat(allItems).isEmpty();
    }

    @Test
    void saveItemTest() {
        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        itemRepository.save(item);
        assertThat(item).isNotNull();
    }

    @Test
    void findItemByIdTest() {
        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        itemRepository.save(item);
        Item itemFromDB = itemRepository.findById(item.getId()).get();
        assertThat(item).isNotNull();
    }

    @Test
    void deleteItemTest() {
        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        itemRepository.save(item);
        itemRepository.deleteById(1L);
        List<Item> allItems = itemRepository.findAll();
        Assertions.assertEquals(0,allItems.size());
    }

    @Test
    void search() {
        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .build();
        Item item2 = Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .build();
        Item item3 = Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> resultList = itemRepository.search("text");
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }

    @Test
    void findAllByOwner() {
        User user1 = User
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();

        User user2 = User
                .builder()
                .id(2L)
                .name("name2")
                .email("email2@mail.com")
                .build();

        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .owner(1L)
                .build();
        Item item2 = Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .owner(1L)
                .build();
        Item item3 = Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .owner(2L)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> resultList = itemRepository.findAllByOwner(item.getOwner());
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }

    @Test
    void findAllByRequestId() {
        Item item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .requestId(1L)
                .build();
        Item item2 = Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .requestId(1L)
                .build();
        Item item3 = Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .requestId(2L)
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> resultList = itemRepository.findAllByRequestId(item.getRequestId());
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }
}
