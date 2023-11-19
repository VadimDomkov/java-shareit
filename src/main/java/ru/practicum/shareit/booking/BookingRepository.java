package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long booker, BookingState status);
}
