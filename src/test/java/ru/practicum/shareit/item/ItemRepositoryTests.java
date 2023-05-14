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
        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build());
        assertThat(item).isNotNull();
    }

    @Test
    void findItemByIdTest() {
        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build());
        Item itemFromDB = itemRepository.findById(item.getId()).get();
        assertThat(itemFromDB).isNotNull();
    }

    @Test
    void deleteItemTest() {
        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .build());
        itemRepository.deleteById(item.getId());
        List<Item> allItems = itemRepository.findAll();
        Assertions.assertEquals(0,allItems.size());
    }

    @Test
    void search() {
        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .build());
        Item item2 = itemRepository.save(Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .build());
        Item item3 = itemRepository.save(Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .build());
        List<Item> resultList = itemRepository.search("text");
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }

    @Test
    void findAllByOwner() {
        User user1 = userRepository.save(User
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build());

        User user2 = userRepository.save(User
                .builder()
                .id(2L)
                .name("name2")
                .email("email2@mail.com")
                .build());

        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .owner(user1.getId())
                .build());
        Item item2 = itemRepository.save(Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .owner(user1.getId())
                .build());
        Item item3 = itemRepository.save(Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .owner(user2.getId())
                .build());

        List<Item> resultList = itemRepository.findAllByOwner(item.getOwner());
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }

    @Test
    void findAllByRequestId() {
        Item item = itemRepository.save(Item
                .builder()
                .id(1L)
                .name("name")
                .description("text")
                .requestId(1L)
                .build());
        Item item2 = itemRepository.save(Item
                .builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .requestId(1L)
                .build());
        Item item3 = itemRepository.save(Item
                .builder()
                .id(3L)
                .name("name3")
                .description("description3")
                .requestId(2L)
                .build());
        List<Item> resultList = itemRepository.findAllByRequestId(item.getRequestId());
        assertThat(resultList).isNotEmpty();
        Assertions.assertEquals(item.getId(),resultList.get(0).getId());
    }
}
