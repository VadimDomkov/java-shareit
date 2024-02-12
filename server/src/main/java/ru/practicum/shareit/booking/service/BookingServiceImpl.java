package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParamException;
import ru.practicum.shareit.exceptions.ValueIsNotEnumException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Long itemId = bookingRequestDto.getItemId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмета с id %d не найдено", itemId)));
        if (item.getAvailable().equals(false)) {
            throw new IncorrectParamException("Предмет недоступен");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().equals(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectParamException("Проверьте корректность дат");
        }
        if (item.getOwner().getId() == userId) {
            throw new EntityNotFoundException(String.format("Предмет с id %d не доступен для бронирования пользователю %d", itemId, userId));
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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запроса с id %d не найдено", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new EntityNotFoundException(String.format("Пользователь %d не владелец вещи %d", userId, booking.getItem().getId()));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new IncorrectParamException(String.format("Запрос %d уже подтвержден", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }


        return bookingMapper.bookingToDto(bookingRepository.save(booking));
    }


    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запроса с id %d не найдено", bookingId)));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Запроса %d для пользователя %d не найдено", bookingId, userId));
        }
        return bookingMapper.bookingToDto(booking);
    }

    public List<BookingDto> getAllBookingsByUserId(Long userId, String state, int from, int size) {
        BookingState bookingState = getStateFromString(state);
        findAndCheckUser(userId);
        List<Booking> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (bookingState) {
            case ALL:
                list = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).toList();
                break;
            case CURRENT:
                list = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, now, now, pageable);
                break;
            case FUTURE:
                list = bookingRepository.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(userId, now, now, pageable);
                break;
            case PAST:
                list = bookingRepository.findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(userId, now, now, pageable);
                break;
            case WAITING:
                list = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                list = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return convertToDtoList(list);
    }


    public List<BookingDto> getAllBookingsForUserItems(Long userId, String state, int from, int size) {

        BookingState bookingState = getStateFromString(state);

        findAndCheckUser(userId);
        List<Booking> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (bookingState) {
            case ALL:
                list = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            case CURRENT:
                list = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                list = bookingRepository.findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                list = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case WAITING:
                list = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                list = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return convertToDtoList(list);
    }

    private User findAndCheckUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
    }

    private List<BookingDto> convertToDtoList(List<Booking> list) {
        return list.stream().map(el -> bookingMapper.bookingToDto(el)).collect(Collectors.toList());
    }

    private BookingState getStateFromString(String state) {
        try {
            return Enum.valueOf(BookingState.class, state);
        } catch (Exception e) {
            throw new ValueIsNotEnumException(String.format("Unknown state: %s", state));
        }
    }
}
