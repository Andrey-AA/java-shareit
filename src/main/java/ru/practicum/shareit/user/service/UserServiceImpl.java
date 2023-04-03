package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.IdentityGenerator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toDTOs(userRepository.getAllUsers());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto);
        log.info("Пользователь успешно добавлен");
        User user = userMapper.toUser(userDto);
        checkEmailExistence(user);
        user.setId(idGenerator());
        user = userRepository.createUser(user);
        return userMapper.toDTO(user);
    }


    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Пользователь успешно обновлен");
        User newUser = userMapper.toUser(userDto);
        checkUserExistence(id);
        newUser.setId(id);
        UserDto user = findUserById(id);

        if (!Objects.equals(newUser.getEmail(),user.getEmail())) {
            checkEmailExistence(newUser);
        }

        if (StringUtils.isBlank(newUser.getEmail())) {
            newUser.setEmail(user.getEmail());
        }

        if (StringUtils.isBlank(newUser.getName())) {
            newUser.setName(user.getName());
        }

        newUser = userRepository.updateUser(newUser, id);
        return userMapper.toDTO(newUser);
    }

    @Override
    public UserDto removeUser(Long id) {
        checkUserExistence(id);
        log.info("Пользователь успешно удален");
        User user = userRepository.removeUser(id);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDto findUserById(long id) {
        log.info("Пользователь успешно найден по ID");
        User user = userRepository.findUserById(id);
        return userMapper.toDTO(user);
    }

    private void checkEmail(UserDto userDto) {
        if (StringUtils.isBlank(userDto.getEmail())) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
    }

    private long idGenerator() {
        return IdentityGenerator.INSTANCE.generateId(User.class);
    }

    public void checkEmailExistence(User user) {

        for (User key: userRepository.getAllUsers()) {
            if (Objects.equals(user.getEmail(), key.getEmail())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с электронной почтой %s уже зарегистрирован.",
                        user.getEmail()));
            }
        }
    }

    public void checkUserExistence(Long userId) {
        if (Objects.isNull(userRepository.findUserById(userId))) {
            throw new EntityNotFoundException(String.format(
                    "Пользователь с id %s не зарегистрирован.",
                    userId));
        }
    }
}