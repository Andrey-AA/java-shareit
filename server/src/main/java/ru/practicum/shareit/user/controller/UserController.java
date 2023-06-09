package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable(value = "id") Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public UserDto removeUser(@PathVariable(value = "id") Long id) {
        return userService.removeUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable(value = "id") Long id) {
        return userService.updateUser(userDto, id);
    }
}
