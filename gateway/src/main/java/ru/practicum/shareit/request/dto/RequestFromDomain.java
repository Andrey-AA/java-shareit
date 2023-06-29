package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RequestFromDomain {
    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}
