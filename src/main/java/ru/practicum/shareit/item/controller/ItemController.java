package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;

    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @GetMapping
    public Collection<ItemDto> findItemsByUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemServiceImpl.findItemsByUser(ownerId);
    }

    @GetMapping ("/{id}")
    public ItemDto findItemById(@PathVariable(value = "id") Long id) {
        return itemServiceImpl.findItemById(id);
    }


    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemServiceImpl.createItem(itemDto,ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable(value = "itemId") Long itemId) {
        return itemServiceImpl.updateItem(itemDto, ownerId, itemId);
    }

    @GetMapping("/search")
        public Collection<ItemDto> search(@RequestParam String text) {
            return itemServiceImpl.search(text);
        }
}