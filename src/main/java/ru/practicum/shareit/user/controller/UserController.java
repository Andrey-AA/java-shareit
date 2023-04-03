package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userServiceImpl.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable(value = "id") Long id) {
        return userServiceImpl.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public UserDto removeUser(@PathVariable(value = "id") Long id) {
        return userServiceImpl.removeUser(id);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable(value = "id") Long id) {
        return userServiceImpl.findUserById(id);
    }
}
