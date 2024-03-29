package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>  {

     Page<Booking> findAllByBookerIdOrderByStartDesc(Long requesterId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long requesterId, BookingStatus state, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
            Long requesterId, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    @Query(value = " SELECT * FROM Bookings WHERE booker = ?1 AND start_date >= ?2 order by start_date DESC ", nativeQuery = true)
    Page<Booking> findAllByBookerAndStartGreaterThanOrderByIdDesc(Long booker, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(
            Long requesterId, LocalDateTime now, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(
            Long requesterId, LocalDateTime now, BookingStatus status);

    Page<Booking> findAllByItemOwnerOrderByStartDesc(Long requester, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartGreaterThanOrderByStartDesc(
            Long requester, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(Long requester, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            Long requester, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(Long requester, BookingStatus state, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus state);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
}