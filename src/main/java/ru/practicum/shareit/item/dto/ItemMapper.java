package ru.practicum.shareit.item.dto;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public Item toItem(@NonNull ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public ItemDto toDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public List<ItemDto> toDTOs(List<Item> items) {
        ArrayList<ItemDto> itemsDto = new ArrayList<>();

        for (Item item: items) {
            itemsDto.add(toDto(item));
        }

        return itemsDto;
    }
}
