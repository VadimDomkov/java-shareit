package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long booker, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long booker, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long booker, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long item, LocalDateTime start, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long item, LocalDateTime start, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long booker, Long item, LocalDateTime start);
}
