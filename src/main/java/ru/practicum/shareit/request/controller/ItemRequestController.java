package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemRequests() {
        return itemRequestService.getAllItemRequests();
    }
}