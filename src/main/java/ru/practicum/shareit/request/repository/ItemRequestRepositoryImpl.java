package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {

    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();

    @Override
    public List<ItemRequest> getAllItemRequests() {
        return new ArrayList<>(itemRequests.values());
    }

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }
}
