package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}
