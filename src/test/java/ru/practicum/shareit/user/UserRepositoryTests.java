package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTests {

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllUsersTest() {
        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).isEmpty();
    }

    @Test
    void saveUserTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        userRepository.save(user);
        assertThat(user).isNotNull();
        //Same: Assertions.assertNotNull(user);
    }

    @Test
    void findUserByIdTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        userRepository.save(user);
        User userFromDB = userRepository.findById(user.getId()).get();
        assertThat(userFromDB).isNotNull();
    }

    @Test
    void deleteUserTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        User user2 = userRepository.save(new User(2L,"name2","email2@mail.com"));
        User user3 = userRepository.save(new User(3L,"name3","email3@mail.com"));
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.deleteById(1L);
        userRepository.deleteById(2L);
        List<User> all = userRepository.findAll();
        Assertions.assertEquals(1,all.size());
    }

    @Test
    void findNameByIdTest() {
        User user = userRepository.save(new User(1L,"name","email@mail.com"));
        User user1 = userRepository.findById(user.getId()).get();
        Assertions.assertNotNull(user1);
    }
}
