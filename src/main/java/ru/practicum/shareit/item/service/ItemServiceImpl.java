package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLong;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long ownerId) {
        checkItemParameters(itemDto);
        userService.checkUserExistence(ownerId);
        log.info("Начата проверка на наличие запроса");
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests();

        for (ItemRequestDto itemRequestDto : requests) {

            if (itemRequestDto.getDescription().equalsIgnoreCase(itemDto.getName())) {
                itemDto.setRequest(itemRequestDto.getId());
            }
        }

        itemDto.setOwner(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        itemRepository.save(item);
        log.info("Вещь успешно добавлена");
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemLong getItemById(Long itemId, Long requesterId) {
        Item item = itemRepository.getReferenceById(itemId);
        checkItemExistence(itemId);
        log.info("Вещь успешно найдена по ID");
        final LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, now);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemId, now, BookingStatus.APPROVED);
        log.info("Проверка на владельца вещи");

        if (!Objects.equals(requesterId, item.getOwner())) {
            lastBooking = null;
            nextBooking = null;
        }

        List<Comment> comments = commentRepository.findAll();
        return ItemMapper.toItemLong(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemLong> findItemsByUser(Long ownerId) {
        List<ItemLong> result = new ArrayList<>();
        log.info("Начался поиск вещей по ID владельца");
        List<Item> userItems = itemRepository.findAllByOwner(ownerId);
        for (Item item : userItems) {
            List<Booking> itemBookings = bookingRepository.findAllByItemId(item.getId());
            result.add(
                    ItemMapper.toLong(item, itemBookings, new ArrayList<>())
            );
        }
        log.info("Вещи успешно найдены по ID владельца");
        return result;
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        userService.checkUserExistence(ownerId);
        checkItemExistence(itemId);
        Item newItem = ItemMapper.toItem(itemDto);

        if (Objects.isNull(itemRepository.getReferenceById(itemId).getId())) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s не зарегистрирована.", itemId));
        }
        newItem.setId(itemId);
        ItemDto item = ItemMapper.toDto(itemRepository.getReferenceById(itemId));

        if (StringUtils.isBlank(newItem.getName())) {
            newItem.setName(item.getName());
        }

        if (StringUtils.isBlank(newItem.getDescription())) {
            newItem.setDescription(item.getDescription());
        }

        if (Objects.isNull(newItem.getAvailable())) {
            newItem.setAvailable(item.getAvailable());
        }

        if (Objects.isNull(newItem.getOwner())) {
            newItem.setOwner(item.getOwner());
        }

        if (StringUtils.isBlank(newItem.getName())) {
            newItem.setRequest(item.getRequest());
        }

        if (!Objects.equals(newItem.getOwner(), ownerId)) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s относится к другому владельцу.", itemId));
        }

        newItem = itemRepository.save(newItem);
        log.info("Вещь успешно обновлена");
        return ItemMapper.toDto(newItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> allItems = itemRepository.search(text);
        ArrayList<Item> searchItems = new ArrayList<>();
        for (Item item : allItems) {
            if (Boolean.TRUE.equals((item.getAvailable()))) {
                searchItems.add(item);
            }
        }
        return ItemMapper.toDTOs(searchItems);
    }

    private void checkItemParameters(ItemDto itemDto) {
        if (Objects.isNull(itemDto.getAvailable()) || Objects.isNull(itemDto.getDescription())
                || Objects.isNull(itemDto.getName()) || itemDto.getDescription().isBlank()
                || itemDto.getName().isBlank()) {
            throw new InvalidItemParametersException("Поля Available, Description и Name не могут быть пустыми.");
        }
    }

    @Override
    public void checkItemExistence(Long itemId) {
        log.info("Поиск вещи");
        ArrayList<Long> itemIds = new ArrayList<>();
        for (Item key : itemRepository.findAll()) {
            itemIds.add(key.getId());
        }

        if (!itemIds.contains(itemId)) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s не зарегистрирована.",
                    itemId));
        }
    }

    @Transactional
    @Override
    public CommentDto saveComment(CommentDto commentDto, Long requesterId, Long itemId) {
        if (StringUtils.isBlank(commentDto.getText())) {
            throw new InvalidItemParametersException("Комментарий не может быть пустым");
        }
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeAndStatusOrderByStartDesc(
                requesterId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new InvalidItemParametersException("Отсутствуют букинги у данного пользователя");
        }

        commentDto.setAuthorName(userRepository.getReferenceById(requesterId).getName());
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = ItemMapper.toComment(commentDto);
        commentRepository.save(comment);
        return ItemMapper.toCommentDto(comment);
    }
}
