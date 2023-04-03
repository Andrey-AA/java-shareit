package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestServiceImpl itemRequestServiceImpl;

    public ItemRequestController(ItemRequestServiceImpl itemRequestServiceImpl) {
        this.itemRequestServiceImpl = itemRequestServiceImpl;
    }

    @PostMapping
    public ItemRequestDto createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestServiceImpl.createItemRequest(itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemRequests() {
        return itemRequestServiceImpl.getAllItemRequests();
    }
}