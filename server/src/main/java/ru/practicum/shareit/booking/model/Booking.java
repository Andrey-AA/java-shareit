package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
