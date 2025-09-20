package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, BookingCreateDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.itemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        if (bookingDto.end().isBefore(bookingDto.start())) {
            throw new ValidationException("Дата окончания раньше начала");
        }
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может подтверждать");  // Changed from NotFoundException to AccessDeniedException
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConflictException("Статус уже изменен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Доступ запрещен");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, String stateStr, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние");
        }
        Pageable pageable = PageRequest.of(from / size, size, org.springframework.data.domain.Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
            case CURRENT -> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable);
            case PAST -> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
            case FUTURE -> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable);
            case WAITING -> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
            case REJECTED -> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
            default -> throw new ValidationException("Неизвестное состояние");
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String stateStr, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние");
        }
        Pageable pageable = PageRequest.of(from / size, size, org.springframework.data.domain.Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findByOwnerId(userId, pageable);
            case CURRENT -> bookings = bookingRepository.findByOwnerIdCurrent(userId, now, pageable);
            case PAST -> bookings = bookingRepository.findByOwnerIdPast(userId, now, pageable);
            case FUTURE -> bookings = bookingRepository.findByOwnerIdFuture(userId, now, pageable);
            case WAITING -> bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED -> bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
            default -> throw new ValidationException("Неизвестное состояние");
        }
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
