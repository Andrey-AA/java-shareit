package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestRepositoryTests {
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAllRequestsTest() {
        List<ItemRequest> allRequest = itemRequestRepository.findAll();
        assertThat(allRequest).isEmpty();
    }

    @Test
    void saveRequestTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();

        itemRequestRepository.save(itemRequest);
        assertThat(itemRequest).isNotNull();
    }

    @Test
    void findRequestByIdTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();
        itemRequestRepository.save(itemRequest);
        ItemRequest itemRequestFromDB = itemRequestRepository.findById(itemRequest.getId()).get();
        assertThat(itemRequestFromDB).isNotNull();
    }

    @Test
    void deleteRequestTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.deleteById(1L);
        List<ItemRequest> all = itemRequestRepository.findAll();
        Assertions.assertEquals(0,all.size());
    }

    @Test
    void getItemRequestByRequesterIdIsNotOrderByCreated() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();
        Pageable pageable = PageRequest.of(0, 20);
        itemRequestRepository.save(itemRequest);
        List<ItemRequest> allRequest = itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(user.getId(),pageable);
        assertThat(allRequest).isEmpty();
    }

    @Test
    void getAllByRequesterIdOrderByCreatedDesc() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .build();
        ItemRequest itemRequest2 = ItemRequest
                .builder()
                .id(2L)
                .description("description")
                .requesterId(1L)
                .build();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
        List<ItemRequest> allRequest = itemRequestRepository.getAllByRequesterIdOrderByCreatedDesc(user.getId());
        assertThat(allRequest).isNotEmpty();
        assertEquals(1L,allRequest.get(0).getId());
        assertEquals(2L,allRequest.get(1).getId());
    }
}