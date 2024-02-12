package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dao.BookingRepository;
//import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParamException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
//@ContextConfiguration(classes = {ItemServiceImpl.class, ItemMapperImpl.class, BookingMapperImpl.class, CommentMapperImpl.class})
class ItemServiceImplTest {

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private RequestRepository requestRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @SpyBean
    ItemMapper itemMapper;

    Item item;

    ItemDto itemDto;

    ItemExtendedDto extendedDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);

        itemDto = ItemDto.builder()
                .name("nameDto")
                .description("descDto")
                .available(true)
                .build();

        extendedDto = ItemExtendedDto.builder()
                .name("extendedName")
                .description("extDesc")
                .available(true)
                .build();
    }

    @Test
    void createItem() {
        itemDto.setRequestId(1L);
        User user = User.builder().build();
        ItemRequest itemRequest = ItemRequest.builder().build();
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemDto result = itemService.createItem(itemDto, 1L);

        assertEquals(item.getName(), result.getName());

        Mockito.verify(requestRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void createItem_withoutRequest() {
        User user = User.builder().build();
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemDto result = itemService.createItem(itemDto, 1L);

        assertEquals(item.getName(), result.getName());

        Mockito.verify(requestRepository, Mockito.never()).findById(anyLong());
    }

    @Test
    void createItem_whenUserNotExists_thenThrowException() {
        Mockito.when(userRepository.findById(1L)).thenThrow(new EntityNotFoundException("Пользователя с id 1 не найдено"));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemService.createItem(itemDto, 1L);
                }
        );

        assertEquals("Пользователя с id 1 не найдено", exception.getMessage());

        Mockito.verify(requestRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void createItem_whenRequestNotExists_thenThrowException() {
        itemDto.setRequestId(1L);
        User user = User.builder().build();
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Mockito.when(requestRepository.findById(1L)).thenThrow(new EntityNotFoundException("Предмета с id 1 у пользователя 1 не найдено"));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemService.createItem(itemDto, 1L);
                }
        );

        assertEquals("Предмета с id 1 у пользователя 1 не найдено", exception.getMessage());

        Mockito.verify(requestRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void updateItem() {
        User user = User.builder().id(1L).build();
        itemDto.setRequestId(1L);
        item.setOwner(user);
        ItemRequest itemRequest = ItemRequest.builder().build();
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemDto result = itemService.updateItem(1L, itemDto, 1L);

        assertEquals(item.getName(), result.getName());
    }

    @Test
    void updateItem_whenOwnerIsNotUser_thenThrowException() {
        User user = User.builder().id(1L).build();
        itemDto.setRequestId(1L);
        item.setOwner(user);
        ItemRequest itemRequest = ItemRequest.builder().build();
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    itemService.updateItem(1L, itemDto, 2L);
                }
        );

        assertEquals("Предмета с id 1 у пользователя 2 не найдено", exception.getMessage());
    }

    @Test
    void getItemById() {
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder()
                .build();
        item.setOwner(user);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(1L, LocalDateTime.now(), BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(1L, LocalDateTime.now(), BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        Mockito.when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

        ItemExtendedDto result = itemService.getItemById(1L, 1L);

        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void getUserItems() {
        UserDto user = UserDto.builder().id(1L).build();
        Mockito.when(userService.getUser(anyLong())).thenReturn(user);
        Mockito.when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemExtendedDto> result = itemService.getUserItems(1L, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void searchItemsByName() {
        Mockito.when(itemRepository.searchByText(any(), any())).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> result = itemService.searchItemsByName("some txt", 1L, 0, 1);

        assertTrue(result.size() == 1);
    }

    @Test
    void searchItemsByName_withEmptyText() {
        List<ItemDto> result = itemService.searchItemsByName("", 1L, 0, 1);

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("comment text")
                .authorName("comment Author")
                .build();
        Comment comment = new Comment();
        comment.setText("commentText");

        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder().build();

        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(List.of(booking));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.saveAndFlush(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertEquals("commentText", result.getText());
    }

    @Test
    void addComment_whenNoBooking_thenThrowException() {
        CommentDto commentDto = CommentDto.builder()
                .text("comment text")
                .authorName("comment Author")
                .build();

        Exception exception = assertThrows(
                IncorrectParamException.class,
                () -> {
                    itemService.addComment(1L, 1L, commentDto);
                }
        );

        assertEquals("Вещь не была арендована", exception.getMessage());
    }
}