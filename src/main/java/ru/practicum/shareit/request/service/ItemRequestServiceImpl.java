package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.utils.IdentityGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestMapper.toDTOs(itemRequestRepository.getAllItemRequests());
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto) {
        log.info("Запрос успешно добавлен");
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setId(idGenerator());
        itemRequest.setCreated(setTime());
        itemRequest = itemRequestRepository.createItemRequest(itemRequest);
        return itemRequestMapper.toDto(itemRequest);
    }

    private long idGenerator() {
        return IdentityGenerator.INSTANCE.generateId(ItemRequest.class);
    }

    private LocalDateTime setTime() {
        return LocalDateTime.now();
    }
}
