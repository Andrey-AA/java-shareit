package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.model.User;
import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    UserDto removeUser(Long id);

    UserDto findUserById(long id);

    void checkEmailExistence(User user);

    void checkUserExistence(Long userId);
}
