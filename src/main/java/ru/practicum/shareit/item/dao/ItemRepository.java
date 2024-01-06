package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query(value = "select it from Item as it where it.available = true " +
            "and (lower(it.name) like lower(concat('%', :text,'%')) " +
            "or lower(it.description) like lower(concat('%', :text,'%')))")
    List<Item> searchByText(@Param("text") String text);
}
