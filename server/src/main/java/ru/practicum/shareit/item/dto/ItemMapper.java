package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item dtoToItem(ItemDto itemDto);

    @Mapping(
            target = "requestId",
            source = "request.id"
    )
    ItemDto itemToDto(Item item);

    ItemExtendedDto itemToExtDto(Item item);
}