package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(@NonNull ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequesterId(),
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto toDto(@NonNull ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequesterId(),
                itemRequest.getCreated()
        );
    }

    public static List<ItemRequestDto> toDTOs(List<ItemRequest> itemRequests) {
        ArrayList<ItemRequestDto> itemRequestsDto = new ArrayList<>();

        for (ItemRequest itemRequest: itemRequests) {
            itemRequestsDto.add(toDto(itemRequest));
        }
        return itemRequestsDto;
    }
}
