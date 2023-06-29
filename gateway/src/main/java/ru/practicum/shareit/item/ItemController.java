package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFromDomain;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemFromDomain itemFromDomain, @RequestHeader(OWNER_ID) Long ownerId) {
        return itemClient.saveItem(itemFromDomain, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@PathVariable(value = "id") Long id, @RequestHeader(OWNER_ID) Long requesterId) {
        return itemClient.getById(id, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByUser(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  @RequestHeader(OWNER_ID) Long ownerId) {
        return itemClient.findItemsByUser(from, size, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemFromDomain itemFromDomain,
                                     @RequestHeader(OWNER_ID) Long ownerId,
                                     @PathVariable(value = "itemId") Long itemId) {
        return itemClient.update(itemFromDomain, ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         @RequestParam(name = "text") String text) {
        return itemClient.search(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader(OWNER_ID) Long requesterId,
                                  @PathVariable(value = "itemId") Long itemId) {
        return itemClient.saveComment(commentDto, itemId, requesterId);
    }
}
