package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.repository.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.IdentityGenerator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepositoryImpl userRepositoryImpl;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toDTOs(userRepositoryImpl.getAllUsers());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto);
        log.info("Пользователь успешно добавлен");
        User user = userMapper.toUser(userDto);
        checkEmailExistence(user);
        user.setId(idGenerator());
        user = userRepositoryImpl.createUser(user);
        return userMapper.toDTO(user);
    }
    
    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Пользователь успешно обновлен");
        User user = userMapper.toUser(userDto);
        checkUserExistence(id);
        user.setId(id);
        checkEmailExistence(user);

        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            user.setEmail(findUserById(id).getEmail());
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(findUserById(id).getName());
        }

        user = userRepositoryImpl.updateUser(user, id);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDto removeUser(Long id) {
        checkUserExistence(id);
        log.info("Пользователь успешно удален");
        User user = userRepositoryImpl.removeUser(id);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDto findUserById(long id) {
        log.info("Пользователь успешно найден по ID");
        User user = userRepositoryImpl.findUserById(id);
        return userMapper.toDTO(user);
    }

    private void checkEmail(UserDto userDto) {
        if (Objects.isNull(userDto.getEmail()) || userDto.getEmail().isBlank()) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
    }

    private long idGenerator() {
        return IdentityGenerator.INSTANCE.generateId(User.class);
    }

    public void checkEmailExistence(User user) {

        for (User key: userRepositoryImpl.getAllUsers()) {
            if (Objects.equals(user.getEmail(), key.getEmail())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с электронной почтой %s уже зарегистрирован.",
                        user.getEmail()));
            }
        }
    }

    public void checkUserExistence(Long userId) {
        if (Objects.isNull(userRepositoryImpl.findUserById(userId))) {
            throw new EntityNotFoundException(String.format(
                    "Пользователь с id %s не зарегистрирован.",
                    userId));
        }
    }
}
