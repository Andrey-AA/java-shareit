package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto getById(Long userId);

    UserDto removeUser(Long id);

    UserDto updateUser(UserDto userDto, Long id);

    void checkEmailExistence(User user);

    void checkUserExistence(Long userId);

    void checkEmail(UserDto userDto);

}
