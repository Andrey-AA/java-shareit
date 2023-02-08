package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems();
    Item createItem(Item item);
    Item updateItem(Item item, Long ownerId, Long itemId);
    Item findItemById(long id);
}
