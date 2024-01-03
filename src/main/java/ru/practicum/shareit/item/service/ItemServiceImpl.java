package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParamException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = itemMapper.dtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId))));
        return itemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto item, Long userId) {

        Item targetItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмета с id %d не найдено", itemId)));
        if (targetItem.getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Предмета с id %d у пользователя %d не найдено", itemId, userId));
        }
        if (item.getName() != null) {
            targetItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            targetItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            targetItem.setAvailable(item.getAvailable());
        }
        return itemMapper.itemToDto(itemRepository.save(targetItem));
    }

    @Override
    public ItemExtendedDto getItemById(long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмета с id %d не найдено", itemId)));

        ItemExtendedDto itemExtendedDto = itemMapper.itemToExtDto(item);

        if (item.getOwner().getId() == userId) {
            setBookingsToItem(itemExtendedDto);
        }
        itemExtendedDto.setComments(getComments(itemId));
        return itemExtendedDto;
    }

    @Override
    public List<ItemExtendedDto> getUserItems(Long userId) {
        userService.getUser(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    ItemExtendedDto dto = itemMapper.itemToExtDto(item);
                    setBookingsToItem(dto);
                    dto.setComments(getComments(item.getId()));
                    return dto;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text, Long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByText(text).stream()
                .map(item -> itemMapper.itemToDto(item))
                .collect(Collectors.toList());
    }

    private void setBookingsToItem(ItemExtendedDto item) {
        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
        item.setNextBooking(bookingMapper.bookingToBookingItemDto(nextBooking));
        item.setLastBooking(bookingMapper.bookingToBookingItemDto(lastBooking));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {

        if (bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).size() == 0) {
            throw new IncorrectParamException("Вещь не была арендована");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.dtoToComment(commentDto);

        comment.setItem(item);
        comment.setAuthor(user);

        return commentMapper.commentToDto(commentRepository.saveAndFlush(comment));
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }
}
