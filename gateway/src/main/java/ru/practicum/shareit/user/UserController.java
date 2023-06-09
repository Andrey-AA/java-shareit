package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserFromDomain;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserFromDomain userFromDomain) {
        return userClient.saveUser(userFromDomain);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable(value = "id") Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeUser(@PathVariable(value = "id") Long id) {
        return userClient.removeUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserFromDomain userFromDomain,
                                             @PathVariable(value = "id") Long id) {
        return userClient.updateUser(userFromDomain, id);
    }
}
