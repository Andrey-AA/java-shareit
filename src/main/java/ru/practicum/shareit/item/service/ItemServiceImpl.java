package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidItemParametersException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.utils.IdentityGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl itemRepositoryImpl;

    private final UserServiceImpl userServiceImpl;
    private final ItemMapper itemMapper;
    private final ItemRequestServiceImpl itemRequestServiceImpl;


    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        checkItemParameters(itemDto);
        userServiceImpl.checkUserExistence(ownerId);

        List<ItemRequestDto> requests = itemRequestServiceImpl.getAllItemRequests();
        for(ItemRequestDto itemRequestDto: requests) {
            if(itemRequestDto.getDescription().equalsIgnoreCase(itemDto.getName())) {
               itemDto.setRequest(itemRequestDto.getId());
            }
        }

        itemDto.setOwner(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(idGenerator());
        itemRepositoryImpl.createItem(item);
        log.info("Вещь успешно добавлена");
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId,Long itemId) {
        userServiceImpl.checkUserExistence(ownerId);
        Item item = itemMapper.toItem(itemDto);

        if (Objects.isNull(itemRepositoryImpl.findItemById(itemId))) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s не зарегистрирована.", itemId));
        }
        item.setId(itemId);
        if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            item.setName(findItemById(itemId).getName());
        }
        if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            item.setDescription(findItemById(itemId).getDescription());
        }
        if (Objects.isNull(item.getAvailable() )) {
            item.setAvailable(findItemById(itemId).getAvailable());
        }
        if (Objects.isNull(item.getOwner())) {
            item.setOwner(findItemById(itemId).getOwner());
        }

        if (Objects.isNull(item.getRequest()) || item.getName().isBlank()) {
            item.setRequest(findItemById(itemId).getRequest());
        }

        if (!Objects.equals(item.getOwner(), ownerId) ) {
            throw new EntityNotFoundException(String.format(
                    "Вещь с id %s относится к другому владельцу.", itemId));
        }

        item = itemRepositoryImpl.updateItem(item, ownerId, itemId);
        log.info("Вещь успешно обновлена");
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto findItemById(long id) {
        Item item = itemRepositoryImpl.findItemById(id);
        log.info("Вещь успешно найдена по ID");
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findItemsByUser(long ownerId) {

        List<Item> allItems = itemRepositoryImpl.getAllItems();
        ArrayList<Item> userItems = new ArrayList<>();

        for(Item item: allItems) {
            if(item.getOwner() == ownerId) {
                userItems.add(item);
            }
        }
        log.info("Вещи успешно найден по ID владельца");
        return itemMapper.toDTOs(userItems);
    }

    @Override
    public List<ItemDto> search(String text) {

        if(text.isEmpty()){
            return new ArrayList<>();
        }

        List<Item> allItems = itemRepositoryImpl.getAllItems();
        ArrayList<Item> searchItems = new ArrayList<>();
        for(Item item: allItems) {
            if((item.getName().toLowerCase().contains(text.trim().toLowerCase())
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
