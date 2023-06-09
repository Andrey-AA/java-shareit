package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestFromDomain;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(OWNER_ID) Long requesterId,
                                               @Valid @RequestBody RequestFromDomain requestFromDomain) {
        return requestClient.saveItemRequest(requesterId, requestFromDomain);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(
            @RequestHeader(OWNER_ID) Long requesterId) {
        return requestClient.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(OWNER_ID) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return requestClient.getAllItemRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(OWNER_ID) Long requesterId,
                                      @PathVariable Long requestId) {
        return requestClient.getItemRequestById(requestId, requesterId);
    }
}