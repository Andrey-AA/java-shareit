package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    @NonNull
    public static UserDto toDTO(@NonNull User user) {
       return new UserDto(
               user.getId(),
               user.getName(),
               user.getEmail(),
               user.getName()
       );
    }

    @NonNull
    public static User toUser(@NonNull UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    @NonNull
    public static List<UserDto> toDTOs(List<User> users) {
        ArrayList<UserDto> usersDto = new ArrayList<>();

        for (User user: users) {
            usersDto.add(toDTO(user));
        }

        return usersDto;
    }
}
