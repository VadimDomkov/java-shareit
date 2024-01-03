package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long booker, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long booker);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long booker, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long item, LocalDateTime start, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long item, LocalDateTime start, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long booker, Long item, LocalDateTime start);
}
