package ru.practicum.shareit.item.dto;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ItemMapper {

    public static Item toItem(@NonNull ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public static ItemDto toDto(@NonNull Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public static List<ItemDto> toDTOs(List<Item> items) {
        ArrayList<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            itemsDto.add(toDto(item));
        }
        return itemsDto;
    }

    public static ItemLong toItemLong(@NonNull Item item,
                                      Booking lastBooking,
                                      Booking nextBooking,
                                      List<CommentDto> comments) {
        return new ItemLong(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                lastBooking != null ? new ItemLong.LastBooking(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId()
                ) : null,
                nextBooking != null ? new ItemLong.NextBooking(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId()
                ) : null,
                comments
        );
    }

    public static ItemLong toLong(Item item, List<Booking> bookings, List<CommentDto> comments) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(now)) {
                lastBooking = booking;
                continue;
            }
            if (booking.getStart().isAfter(now)) {
                nextBooking = booking;
                continue;
            }
            if (lastBooking != null && nextBooking != null) {
                break;
            }
        }
        return toItemLong(item, lastBooking, nextBooking, comments);
    }

    public static CommentDto toCommentDto(@NonNull Comment comment, @NonNull UserRepository userRepository) {
        String authorName = userRepository.findNameById(comment.getAuthorId());

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                authorName,
                comment.getAuthorId(),
                comment.getCreated()
        );
    }

    public static Comment toComment(@NonNull CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItemId(),
                commentDto.getAuthorId(),
                commentDto.getCreated()
        );
    }

}
