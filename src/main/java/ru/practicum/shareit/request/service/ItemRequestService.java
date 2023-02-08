package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getAllItemRequests();
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);
}
