package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTests {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    private Item item;

    private User user2;

    private Booking booking;

    @BeforeEach
    void init() {

        user = new User(1L,"name", "email@email.com");
        item = new Item(1L,"name","description",true,user.getId(),1L);
        user2 = new User(2L,"name2", "email2@email.com");
        booking = new Booking(1L, LocalDateTime.of(2023, 1, 10, 10, 30),
                LocalDateTime.of(2023, 3, 10, 10, 30), item, user2, WAITING);
    }

    @Test
    void findBookingsByUser() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        List<Booking> bookingsByUser = bookingRepository.findBookingsByUser(user.getId(), Pageable.ofSize(10));
        assertThat((long) bookingRepository.findBookingsByUser(user2.getId(), Pageable.ofSize(10)).size(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndEndBeforeAndStatusOrderByStartDescTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
        assertThat(bookingRepository
                        .findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(user2.getId(),
                                LocalDateTime.of(2023, 4, 10, 10, 10), APPROVED).size(),
                equalTo(1));
    }

    @Test
    void findAllByBookerIdAndEndBeforeAndStatusOrderByStartDescPageableTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(user2.getId(),
                LocalDateTime.of(2023, 4, 10, 10, 10), WAITING,
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStartGreaterThanOrderByStartDescTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndStartGreaterThanOrderByStartDesc(user2.getId(),
                LocalDateTime.now(), Pageable.ofSize(10)).stream().count(), equalTo(0L));
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAscTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(user2.getId(),
                LocalDateTime.of(2023, 2, 1, 10, 10), LocalDateTime.now(),
                Pageable.ofSize(10)).stream().count(), equalTo(0L));
    }

    @Test
    void findFirstByItemIdAndStartBeforeOrderByStartDescTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now()).getId(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user.getId(), WAITING, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }
}
