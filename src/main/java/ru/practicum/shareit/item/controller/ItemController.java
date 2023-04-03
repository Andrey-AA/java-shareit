package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) Long ownerId) {
        return itemService.saveItem(itemDto,ownerId);
    }

    @GetMapping ("/{id}")
    public ItemLong findItemById(@PathVariable(value = "id") Long id,  @RequestHeader(OWNER_ID) Long requesterId) {
        return itemService.getItemById(id, requesterId);
    }

    @GetMapping
    public Collection<ItemLong> findItemsByUser(@RequestHeader(OWNER_ID) Long ownerId) {
        return itemService.findItemsByUser(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(OWNER_ID) Long ownerId,
                              @PathVariable(value = "itemId") Long itemId) {
        return itemService.updateItem(itemDto, ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                              @RequestHeader(OWNER_ID) Long requesterId,
                              @PathVariable(value = "itemId") Long itemId) {
        return itemService.saveComment(commentDto, requesterId, itemId);
    }
}
