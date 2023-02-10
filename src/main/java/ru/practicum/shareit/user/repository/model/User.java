package ru.practicum.shareit.user.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    @Email(message = "Email is incorrect")
    private String email;
}