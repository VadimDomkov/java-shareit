package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = false)
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", bookerId)));
        ItemRequest itemRequest = requestMapper.dtoToRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return requestMapper.itemToDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long requestOwnerId) {
        userRepository.findById(requestOwnerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", requestOwnerId)));
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestOwnerId)
                .stream().map(this::convertToExtendedDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return requestRepository.findAllByRequestorIdNot(userId, pageable).stream().map(this::convertToExtendedDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return convertToExtendedDto(requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с id %d не найден", requestId))));
    }

    private ItemRequestDto convertToExtendedDto(ItemRequest itemRequest) {
        List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(itemRequest.getId())
                .stream().map(item -> itemMapper.itemToDto(item)).collect(Collectors.toList());
        ItemRequestDto dto = requestMapper.itemToDto(itemRequest);
        dto.setItems(itemDtoList);
        return dto;
    }
}
