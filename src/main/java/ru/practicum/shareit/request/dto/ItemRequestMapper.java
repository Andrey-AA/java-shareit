package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getCreated()
        );
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }

    public List<ItemRequestDto> toDTOs(List<ItemRequest> itemRequests) {
        ArrayList<ItemRequestDto> itemRequestsDto = new ArrayList<>();

        for(ItemRequest itemRequest: itemRequests) {
            itemRequestsDto.add(toDto(itemRequest));
        }
        return itemRequestsDto;
    }
}
