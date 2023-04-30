package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        return ItemRequestMapper.toDTOs(itemRequests);
    }

    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto) {
        log.info("Запрос успешно добавлен");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(setTime());
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    private LocalDateTime setTime() {
        return LocalDateTime.now();
    }
}
