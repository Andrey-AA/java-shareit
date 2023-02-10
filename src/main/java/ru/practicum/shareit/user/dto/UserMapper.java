package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.repository.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserDto toDTO(User user) {
       return new UserDto(
               user.getId(),
               user.getName(),
               user.getEmail(),
               user.getName()
       );
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public List<UserDto> toDTOs(List<User> users) {
        ArrayList<UserDto> usersDto = new ArrayList<>();

        for (User user: users) {
            usersDto.add(toDTO(user));
        }

        return usersDto;
    }
}
