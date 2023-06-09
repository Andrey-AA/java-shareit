package ru.practicum.shareit.user.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.toDTOs(users);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkEmail(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDTO(repository.save(user));
    }

    @Override
    public UserDto getById(Long userId) {
        checkUserExistence(userId);
        User user = repository.getReferenceById(userId);
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDto removeUser(Long id) {
        checkUserExistence(id);
        User user = repository.getReferenceById(id);
        repository.deleteById(id);
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User newUser = UserMapper.toUser(userDto);
        checkUserExistence(id);
        newUser.setId(id);
        User oldUser = repository.getReferenceById(id);

        if (!Objects.equals(newUser.getEmail(),oldUser.getEmail())) {
            checkEmailExistence(newUser);
        }

        if (StringUtils.isBlank(newUser.getEmail())) {
            newUser.setEmail(oldUser.getEmail());
        }

        if (StringUtils.isBlank(newUser.getName())) {
            newUser.setName(oldUser.getName());
        }

        repository.save(newUser);
        log.info("Пользователь успешно обновлен");
        return UserMapper.toDTO(newUser);
    }

    @Override
    public void checkEmailExistence(User user) {
        for (User key: repository.findAll()) {
            if (Objects.equals(user.getEmail(), key.getEmail())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с электронной почтой %s уже зарегистрирован.",
                        user.getEmail()));
            }
        }
    }

    public void checkEmail(UserDto userDto) {
        if (StringUtils.isBlank(userDto.getEmail())) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
    }

    @Override
    public void checkUserExistence(Long userId) {
        log.info("Поиск пользователя");
        repository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id %s не зарегистрирован.", userId)));
    }
}