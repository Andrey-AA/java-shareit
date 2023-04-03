package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long ownerId);

    ItemLong getItemById(Long itemId, Long requesterId);

    List<ItemLong> findItemsByUser(Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    List<ItemDto> search(String text);

    void checkItemExistence(Long itemId);

    CommentDto saveComment(CommentDto commentDto, Long requesterId, Long itemId);
}
