package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectBookingStatusException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.IdentityGenerator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository repository;

    @Override
    public List<BookingDto> getAllBookings() {
        log.info("Все бронирования найдены");
        return bookingMapper.toDTOs(bookingRepository.getAllBookings());
    }

    @Override
    public BookingDto findBookingById(Long id) {
        log.info("Бронирование успешно найдено по ID");
        Booking booking = bookingRepository.findBookingById(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        log.info("Поступил запрос на создание нового бронирования");
        Booking booking = bookingMapper.toBooking(bookingDto);

        log.info("Началась проверка возможности бронирования вещи");

        if ((Boolean.FALSE.equals(repository.findItemById(booking.getItem()).getAvailable()))
                || (Objects.equals(booking.getBooker(), repository.findItemById(booking.getItem()).getOwner()))) {
            throw new ItemNotAvailableException(String.format(
                    "Вещь с id %s недоступна для бронирования.", repository.findItemById(booking.getItem()).getId()));
        }

        log.info("Проверка доступности завершена успешно");
        booking.setId(idGenerator());
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.createBooking(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto, Long userId, Long bookingId) {
        log.info("Поступил запрос на изменение нового бронирования");
        userService.checkUserExistence(userId);

        Booking booking = bookingMapper.toBooking(bookingDto);

        if (bookingDto.getStatus() != BookingStatus.WAITING) {
            booking.setId(bookingId);

            log.info("Началась проверка возможности изменения статуса бронирования");

            if ((booking.getStatus() == (BookingStatus.APPROVED))
                    || (booking.getStatus() == (BookingStatus.REJECTED))) {

                if (!Objects.equals(userId, repository.findItemById(booking.getItem()).getOwner())) {
                    throw new IncorrectBookingStatusException("запрошен некорректный статус бронирования");
                }
                booking.setStatus(booking.getStatus());
            }

            if (booking.getStatus() == (BookingStatus.CANCELED)) {

                if (!Objects.equals(userId, booking.getBooker())) {
                    throw new IncorrectBookingStatusException("запрошен некорректный статус бронирования");
                }
                booking.setStatus(BookingStatus.CANCELED);
            }
            log.info("Изменение статуса завершено ");
            bookingRepository.updateBooking(booking, userId, bookingId);
        } else {
            throw new IncorrectBookingStatusException("запрошен некорректный статус бронирования ");
        }
        return bookingMapper.toDto(booking);
    }

    private long idGenerator() {
        return IdentityGenerator.INSTANCE.generateId(Booking.class);
    }
}
