package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository {
    List<ItemRequest> getAllItemRequests();
    ItemRequest createItemRequest(ItemRequest itemRequest);
}
