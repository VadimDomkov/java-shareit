package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ItemRequest dtoToRequest(ItemRequestDto dto);

    ItemRequestDto itemToDto(ItemRequest itemRequest);
}
