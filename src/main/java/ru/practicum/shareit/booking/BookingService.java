package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Long itemId = bookingRequestDto.getItemId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмета с id %d не найдено", itemId)));
        if (item.getAvailable().equals(false)) {
            throw new ItemIsNotAvailableException("Предмет недоступен");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().equals(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectParamException("Проверьте корректность дат");
        }
        Booking booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        return bookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Запроса с id %d не найдено", bookingId)));
        if (!userId.equals(booking.getItem().getOwner())) {
            throw new UserIsNotAnOwnerException(String.format("Пользователь %d не владелец вещи %d", userId, booking.getItem().getId()));
        }
        booking.setStatus(BookingStatus.APPROVED);
        return bookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        return bookingMapper.bookingToDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Запроса с id %d не найдено", bookingId))));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        BookingState bookingState = Enum.valueOf(BookingState.class, state);
        findAndCheckUser(userId);
        List<Booking> list = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                list = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                list = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                list = bookingRepository.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                list = bookingRepository.findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                case REJECTED:
                list = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, bookingState);
                break;
            default:
                throw new IncorrectParamException("Unknown state: UNSUPPORTED_STATUS");
        }
        return convertToDtoList(list);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsForUserItems(Long userId, String state) {
        findAndCheckUser(userId);
        return null;
    }

    private User findAndCheckUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
    }

    private List<BookingDto> convertToDtoList(List<Booking> list) {
        return list.stream().map(el -> bookingMapper.bookingToDto(el)).collect(Collectors.toList());
    }
}
