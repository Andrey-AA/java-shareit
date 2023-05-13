package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

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

    public static ItemRequestFull toItemRequestFull(@NonNull ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestFull(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequesterId(),
                itemRequest.getCreated(),
                items
        );
    }

    public static List<ItemRequestFull> toFulls(@NonNull List<ItemRequest> itemRequests, ItemRepository itemRepository) {
        List<ItemRequestFull> itemRequestFulls = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByRequestId(itemRequest.getRequesterId());
            itemRequestFulls.add(ItemRequestMapper.toItemRequestFull(itemRequest,items));
        }

        return itemRequestFulls;
    }





}
