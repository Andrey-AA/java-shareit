package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemLong {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    @ToString.Exclude
    private List<CommentDto> comments;

    @Data
    public static class LastBooking {
        private final long id;
        private final long bookerId;
    }

    @Data
    public static class NextBooking {
        private final long id;
        private final long bookerId;
    }
}

