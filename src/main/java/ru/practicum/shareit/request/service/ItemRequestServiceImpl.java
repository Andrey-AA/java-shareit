package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestFull;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final BookingRepository bookingRepository;

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        return ItemRequestMapper.toDTOs(itemRequests);
    }

    @Override
    public List<ItemRequestFull> getItemRequestsByUserId(Long requesterId) {
        log.info("Проверка пользователя");
        userService.checkUserExistence(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequesterIdOrderByCreatedDesc(requesterId);
        return ItemRequestMapper.toFulls(itemRequests, itemRepository);
    }

    @Override
    public List<ItemRequestFull> getAllItemRequestsWithPagination(Long requesterId, Integer from, Integer size) {
        BookingServiceImpl.checkPagination(from, size);
        log.info("Проверка пользователя");
        userService.checkUserExistence(requesterId);
        log.info("Пагинация");
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository
                .getItemRequestByRequesterIdIsNotOrderByCreated(requesterId, pageable);
        return ItemRequestMapper.toFulls(itemRequests, itemRepository);
    }

    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        log.info("Проверка существования пользователя");
        userService.checkUserExistence(requesterId);
        log.info("Проверка наличия описания");

        if (itemRequestDto.getDescription().isBlank()) {
            throw new InvalidItemParametersException("Поле Description не может быть пустым.");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequesterId(requesterId);
        itemRequest.setCreated(setTime());
        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Запрос успешно добавлен");
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public ItemRequestFull getItemRequestById(Long requesterId, Long requestId) {
        log.info("Проверка существования пользователя");
        userService.checkUserExistence(requesterId);
        if (requestId == null) {
            throw new InvalidItemParametersException("Не указан ID пользователя.");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос по ID не найден ."));

        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemRequestFull(itemRequest, items);
    }

    private LocalDateTime setTime() {
        return LocalDateTime.now();
    }
}
