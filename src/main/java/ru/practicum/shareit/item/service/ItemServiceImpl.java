package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.IdentityGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRequestService itemRequestService;


    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        checkItemParameters(itemDto);
        userService.checkUserExistence(ownerId);
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests();

        for (ItemRequestDto itemRequestDto : requests) {

            if (itemRequestDto.getDescription().equalsIgnoreCase(itemDto.getName())) {
                itemDto.setRequest(itemRequestDto.getId());
            }
        }

        itemDto.setOwner(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(idGenerator());
        itemRepository.createItem(item);
        log.info("Вещь успешно добавлена");
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        userService.checkUserExistence(ownerId);
        Item newItem = itemMapper.toItem(itemDto);

        if (Objects.isNull(itemRepository.findItemById(itemId))) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s не зарегистрирована.", itemId));
        }
        newItem.setId(itemId);
        ItemDto item = findItemById(itemId);

        if (StringUtils.isEmpty(newItem.getName()) || StringUtils.isBlank(newItem.getName())) {
            newItem.setName(item.getName());
        }


        if (StringUtils.isEmpty(newItem.getDescription()) || StringUtils.isBlank(newItem.getDescription())) {
            newItem.setDescription(item.getDescription());
        }

        if (Objects.isNull(newItem.getAvailable())) {
            newItem.setAvailable(item.getAvailable());
        }


        if (Objects.isNull(newItem.getOwner())) {
            newItem.setOwner(item.getOwner());
        }

        if (Objects.isNull(newItem.getRequest()) ||  StringUtils.isBlank(newItem.getName())) {
            newItem.setRequest(item.getRequest());
        }

        if (!Objects.equals(newItem.getOwner(), ownerId)) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s относится к другому владельцу.", itemId));
        }

        newItem = itemRepository.updateItem(newItem, ownerId, itemId);
        log.info("Вещь успешно обновлена");
        return itemMapper.toDto(newItem);
    }

    @Override
    public ItemDto findItemById(long id) {
        Item item = itemRepository.findItemById(id);
        log.info("Вещь успешно найдена по ID");
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findItemsByUser(long ownerId) {
        List<Item> allItems = itemRepository.getAllItems();
        ArrayList<Item> userItems = new ArrayList<>();

        for (Item item : allItems) {
            if (item.getOwner() == ownerId) {
                userItems.add(item);
            }
        }
        log.info("Вещи успешно найден по ID владельца");
        return itemMapper.toDTOs(userItems);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> allItems = itemRepository.getAllItems();
        ArrayList<Item> searchItems = new ArrayList<>();
        for (Item item : allItems) {
            if ((item.getName().toLowerCase().contains(text.trim().toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.trim().toLowerCase()))
                    && Boolean.TRUE.equals((item.getAvailable()))) {
                searchItems.add(item);
            }
        }
        return itemMapper.toDTOs(searchItems);
    }

    private void checkItemParameters(ItemDto itemDto) {
        if (Objects.isNull(itemDto.getAvailable()) || Objects.isNull(itemDto.getDescription())
                || Objects.isNull(itemDto.getName()) || itemDto.getDescription().isBlank()
                || itemDto.getName().isBlank()) {
            throw new InvalidItemParametersException("Поля Available, Description и Name не могут быть пустыми.");
        }
    }

    private long idGenerator() {
        return IdentityGenerator.INSTANCE.generateId(Item.class);
    }

}
