package ru.practicum.shareit.request.dto;

import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId).collect(Collectors.toList()));

        for (ItemRequest itemRequest : itemRequests) {
            itemRequestFulls.add(ItemRequestMapper.toItemRequestFull(
                    itemRequest, items.stream()
                            .filter(item -> item.getRequestId().equals(itemRequest.getId())).collect(Collectors.toList()))
            );
        }

        return itemRequestFulls;
    }





}
