package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>  {

     @Query(value = " SELECT * FROM Bookings WHERE booker like ?1 order by start_date DESC ", nativeQuery = true)
    List<Booking> findBookingsByUser(Long booker);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long requesterId, BookingStatus state);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long requesterId, LocalDateTime now, LocalDateTime now2);

    @Query(value = " SELECT * FROM Bookings WHERE booker = ?1 AND start_date >= ?2 order by start_date DESC ", nativeQuery = true)
    List<Booking> findAllByBookerAndStartGreaterThanOrderByIdDesc(Long booker, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(Long requesterId, LocalDateTime now, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(Long requester);

    List<Booking> findAllByItemOwnerAndStartGreaterThanOrderByStartDesc(Long requester, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(Long requester, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long requester, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(Long requester, BookingStatus state);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus state);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
}