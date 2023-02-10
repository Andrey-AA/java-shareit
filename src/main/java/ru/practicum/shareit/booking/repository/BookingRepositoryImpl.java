package ru.practicum.shareit.booking.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import java.util.*;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

    private final Map<Long, Booking> bookings = new HashMap<>();
    private final ItemRepositoryImpl itemRepository;

    @Autowired
    public BookingRepositoryImpl(ItemRepositoryImpl itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public Booking findBookingById(Long id) {
        return bookings.get(id);
    }

    @Override
    public Booking createBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
        itemRepository.findItemById(booking.getItem()).setAvailable(false);
        log.info("Установлен статус 'WAITING'. Ожидается подтверждение бронирования владельцем");
            return booking;
    }

    @Override
    public Booking updateBooking(Booking booking, Long userId, Long bookingId) {
        bookings.put(bookingId, booking);
        return booking;
    }
}
