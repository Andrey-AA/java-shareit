package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.UserDto;
import ru.practicum.shareit.request.model.ItemRequestFull;
import ru.practicum.shareit.request.service.ItemRequestService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(OWNER_ID) Long requesterId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestFull> getItemRequestsByUserId(
            @RequestHeader(OWNER_ID) Long requesterId) {
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestFull> getAllItemRequests(
            @RequestHeader(OWNER_ID) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return itemRequestService.getAllItemRequestsWithPagination(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestFull getItemRequestById(@RequestHeader(OWNER_ID) Long requesterId,
                                      @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requesterId, requestId);
    }
}