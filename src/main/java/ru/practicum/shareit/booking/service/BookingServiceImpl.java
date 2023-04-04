package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingFromDomain;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    public List<BookingShort> getAllBookings() {
        log.info("Все бронирования найдены");
        return BookingMapper.toBookingShorts(bookingRepository.findAll());
    }

    @Override
    @Transactional
    public BookingShort createBooking(BookingFromDomain bookingFromDomain, Long bookerId) {
        log.info("Поступил запрос на создание нового бронирования");
        itemService.checkItemExistence(bookingFromDomain.getItemId());
        Booking booking = BookingMapper.toBooking(bookingFromDomain, ItemMapper.toItem(
                ItemMapper.toDto(itemRepository.getReferenceById(bookingFromDomain.getItemId()))),
                UserMapper.toUser(userService.getById(bookerId)));
        log.info("Началась проверка существования пользователей");
        userService.checkUserExistence(bookerId);
        log.info("Началась проверка корректности дат бронирования");

        if((booking.getEnd().isBefore(LocalDateTime.now())) || (booking.getEnd().isBefore(booking.getStart()))
                || (booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new ItemNotAvailableException("Указаны некорректные даты бронирования");
        }

        log.info("Началась проверка возможности бронирования вещи");

        if (Boolean.FALSE.equals(itemRepository.getReferenceById(booking.getItem().getId()).getAvailable())) {
            throw new InvalidItemParametersException(String.format(
                    "Вещь с id %s недоступна для бронирования.", itemRepository.getReferenceById(
                            booking.getItem().getId()).getId()));
        }

        log.info("Проверка доступности вещи успешно завершена");

        if (Objects.equals(bookerId, itemRepository.getReferenceById(booking.getItem().getId()).getOwner())) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s недоступна для бронирования.", itemRepository.getReferenceById(
                            booking.getItem().getId()).getId()));
        }

        log.info("Проверка доступности завершена успешно");
        User booker = userRepository.getReferenceById(bookerId);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingShort(booking);
    }

    @Override
    public BookingShort findBookingById(Long bookingId, Long requesterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));
        if ((Objects.equals(requesterId, booking.getBooker().getId()))
                || (Objects.equals(requesterId, itemRepository.getReferenceById(
                        booking.getItem().getId()).getOwner()))) {
            log.info("Бронирование успешно найдено по ID");
        } else {
            throw new IncorrectParameterException("Бронирование доступно только автору или владельцу вещи");
        }

        return BookingMapper.toBookingShort(booking);
    }

    @Override
    @Transactional
    public BookingShort approveBooking(Long bookingId, boolean approved, Long requesterId) {
        log.info("Поступил запрос на изменение нового бронирования");
        userService.checkUserExistence(requesterId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено по ID"));

        if (Objects.equals(requesterId, itemRepository.getReferenceById(booking.getItem().getId()).getOwner())) {
            log.info("Проверка на владельца вещи завершена успешно");
        } else {
            throw new IncorrectParameterException("Подтвердить бронирование может только владелец вещи");
        }

        log.info("Проверка текущего статуса");

        if (approved) {
            if (Objects.equals(booking.getStatus(), BookingStatus.APPROVED)) {
                throw new InvalidItemParametersException("Статус бронирования уже подтвержден");
            }
         booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        log.info("Изменение статуса завершено ");
        bookingRepository.save(booking);
        return BookingMapper.toBookingShort(booking);
    }

    @Override
    @Transactional
    public BookingShort cancelBooking(Long bookingId, boolean canceled, Long requesterId) {
        log.info("Поступил запрос на отмену бронирования");
        userService.checkUserExistence(requesterId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено по ID"));

        if (Objects.equals(requesterId, booking.getBooker().getId())) {
            log.info("Проверка на автора бронирвания завершена успешно");
        } else {
            throw new IncorrectParameterException("Отменить бронирование может только автор вещи");
        }

        if (canceled) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingShort(booking);
    }

    @Override
    public List<BookingShort> findBookingsByUser(String state, Long requesterId) {
        log.info("Запрос на поиск бронирований пользователя");
        userService.checkUserExistence(requesterId);
        log.info("Провека на существование пользователя завершена успешно");
        final LocalDateTime now = LocalDateTime.now();

        List<Booking> userBookings = new ArrayList<>();

        switch (BookingState.valueOf(state)) {
            case ALL: {
                log.info("Статус 'ALL'");
                userBookings = bookingRepository.findBookingsByUser(requesterId);
                break;
            }
            case WAITING: {
                log.info("Статус 'WAITING'");
                userBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        requesterId,BookingStatus.valueOf(state));
                break;
            }
            case REJECTED: {
                log.info("Статус 'REJECTED'");
                userBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        requesterId,BookingStatus.valueOf(state));
                break;
            }
            case CURRENT: {
                log.info("Статус 'CURRENT'");
                userBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        requesterId, now, now);
                break;
            }
            case FUTURE: {
                log.info("Статус 'FUTURE'");
                userBookings = bookingRepository.findAllByBookerAndStartGreaterThanOrderByIdDesc(
                        userRepository.getReferenceById(requesterId).getId(), now);
                break;
            }
            case PAST: {
                log.info("Статус 'PAST'");
                userBookings = bookingRepository.findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(
                        requesterId, now, BookingStatus.APPROVED);
                break;
            }
        }
        return BookingMapper.toBookingShorts(userBookings);
    }

    @Override
    public List<BookingShort> findBookingsByOwner(String state, Long requesterId) {
        log.info("Запрос на поиск бронирований по владельцу");
        userService.checkUserExistence(requesterId);
        log.info("Провека на существование пользователя завершена успешно");
        List<Booking> ownerBookings = new ArrayList<>();
        final LocalDateTime now = LocalDateTime.now();
        switch (BookingState.valueOf(state)) {
            case ALL: {
                log.info("Статус 'ALL'");
                ownerBookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(requesterId);
                break;
            }
            case FUTURE: {
                log.info("Статус 'FUTURE'");
                ownerBookings = bookingRepository.findAllByItemOwnerAndStartGreaterThanOrderByStartDesc(
                        requesterId, now);
                break;
            }
            case PAST: {
                log.info("Статус 'PAST'");
                ownerBookings = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(requesterId, now);
                break;
            }
            case CURRENT: {
                log.info("Статус 'CURRENT'");
                ownerBookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        requesterId, now, now);
                break;
            }
            case WAITING: {
                log.info("Статус 'WAITING'");
                ownerBookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        requesterId,BookingStatus.valueOf(state));
                break;
            }
            case REJECTED: {
                log.info("Статус 'REJECTED'");
                ownerBookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        requesterId,BookingStatus.valueOf(state));
                break;
            }
        }
        return BookingMapper.toBookingShorts(ownerBookings);
    }
}
