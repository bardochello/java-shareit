package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemCreateDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.requestId() != null) {
            ru.practicum.shareit.request.model.ItemRequest request = itemRequestRepository.findById(itemDto.requestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(request);
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не владелец");
        }
        itemMapper.updateItem(itemDto, item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не владелец");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemWithBookingsDto getItemWithBookingsById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        LocalDateTime now = LocalDateTime.now();
        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            Optional<ru.practicum.shareit.booking.model.Booking> last = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId, now, BookingStatus.APPROVED);
            Optional<ru.practicum.shareit.booking.model.Booking> next = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED);
            lastBooking = last.map(bookingMapper::toBookingShortDto).orElse(null);
            nextBooking = next.map(bookingMapper::toBookingShortDto).orElse(null);
        }
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto).collect(Collectors.toList());
        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    @Override
    public List<ItemWithBookingsDto> getItemsWithBookingsByOwner(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);
        LocalDateTime now = LocalDateTime.now();
        return items.stream().map(item -> {
            Optional<ru.practicum.shareit.booking.model.Booking> last = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(item.getId(), now, BookingStatus.APPROVED);
            Optional<ru.practicum.shareit.booking.model.Booking> next = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), now, BookingStatus.APPROVED);
            List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                    .map(commentMapper::toCommentDto).collect(Collectors.toList());
            return new ItemWithBookingsDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    last.map(bookingMapper::toBookingShortDto).orElse(null),
                    next.map(bookingMapper::toBookingShortDto).orElse(null),
                    comments
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.search(text, pageable).stream()
                .map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.findByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId, now, BookingStatus.APPROVED).isEmpty()) {
            throw new ValidationException("Пользователь не бронировал вещь или бронирование не завершено");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);
        Comment saved = commentRepository.save(comment);
        return new CommentDto(
                saved.getId(),
                saved.getText(),
                saved.getAuthor().getName(),
                saved.getCreated()
        );
    }
}
