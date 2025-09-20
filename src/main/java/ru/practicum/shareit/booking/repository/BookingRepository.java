package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Репозиторий для бронирований
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Бронирования пользователя по статусу
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    // Все бронирования пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // Текущие бронирования пользователя
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    // Прошлые бронирования пользователя
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    // Будущие бронирования пользователя
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    // Бронирования владельца по статусу
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    // Все бронирования владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    // Текущие бронирования владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findByOwnerIdCurrent(Long ownerId, LocalDateTime now, Pageable pageable);

    // Прошлые бронирования владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findByOwnerIdPast(Long ownerId, LocalDateTime now, Pageable pageable);

    // Будущие бронирования владельца
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findByOwnerIdFuture(Long ownerId, LocalDateTime now, Pageable pageable);

    // Последнее бронирование вещи
    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    // Следующее бронирование вещи
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    // Проверка, брал ли пользователь вещь
    List<Booking> findByBookerIdAndItemIdAndEndBeforeAndStatus(Long userId, Long itemId, LocalDateTime now, BookingStatus status);
}
