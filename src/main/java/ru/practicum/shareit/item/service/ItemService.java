package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);
    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);
    ItemDto findItemById(long id);
    List<ItemDto> findItemsByUser(long ownerId);
    List<ItemDto> search(String text);

}
