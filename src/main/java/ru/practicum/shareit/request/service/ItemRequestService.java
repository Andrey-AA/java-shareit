package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestFull;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAllItemRequests();

    List<ItemRequestFull> getItemRequestsByUserId(Long requesterId);

    List<ItemRequestFull> getAllItemRequestsWithPagination(Long requesterId, Integer from, Integer size);

    ItemRequestDto saveItemRequest(Long requesterId, ItemRequestDto itemRequestDto);

    ItemRequestFull getItemRequestById(Long requesterId, Long requestId);
}
