package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user, Long id);

    User removeUser(Long id);

    User findUserById(long id);
}
