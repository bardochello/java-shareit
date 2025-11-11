package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRequestService itemRequestService;

    private User owner;
    private User booker;
    private ItemCreateDto createDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);
        createDto = new ItemCreateDto("Item", "Desc", true, null);
    }

    @Test
    void addItemShouldSaveAndReturnDto() {
        ItemDto result = itemService.addItem(owner.getId(), createDto);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Item");
        assertThat(result.available()).isTrue();
    }

    @Test
    void addItemWithRequestIdShouldLinkToRequest() {
        ItemRequestCreateDto reqDto = new ItemRequestCreateDto("Need item");
        itemRequestService.addRequest(booker.getId(), reqDto);
        createDto = new ItemCreateDto("Item", "Desc", true, 1L); // Assume req id=1

        ItemDto result = itemService.addItem(owner.getId(), createDto);

        assertThat(result.requestId()).isEqualTo(1L);
    }

    @Test
    void addItemWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> itemService.addItem(999L, createDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void updateItemShouldUpdateFields() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);
        ItemUpdateDto updateDto = new ItemUpdateDto("New Name", "New Desc", false);

        ItemDto result = itemService.updateItem(owner.getId(), created.id(), updateDto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.description()).isEqualTo("New Desc");
        assertThat(result.available()).isFalse();
    }

    @Test
    void updateItemWhenNotOwnerThenThrowNotFound() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);
        ItemUpdateDto updateDto = new ItemUpdateDto("New", null, null);

        assertThatThrownBy(() -> itemService.updateItem(booker.getId(), created.id(), updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Не владелец");
    }

    @Test
    void deleteItemShouldRemoveItem() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);

        itemService.deleteItem(owner.getId(), created.id());

        assertThat(itemRepository.findById(created.id())).isEmpty();
    }

    @Test
    void deleteItemWhenNotOwnerThenThrowNotFound() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);

        assertThatThrownBy(() -> itemService.deleteItem(booker.getId(), created.id()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Не владелец");
    }

    @Test
    void getItemWithBookingsByIdShouldReturnWithBookingsForOwner() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);
        BookingCreateDto bookingDto = new BookingCreateDto(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), created.id());
        BookingDto booking = bookingService.addBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), booking.id(), true);

        ItemWithBookingsDto result = itemService.getItemWithBookingsById(owner.getId(), created.id());

        assertThat(result.lastBooking()).isNotNull();
        assertThat(result.nextBooking()).isNull();
        assertThat(result.comments()).isEmpty();
    }

    @Test
    void getItemWithBookingsByIdForNonOwnerShouldReturnWithoutBookings() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);

        ItemWithBookingsDto result = itemService.getItemWithBookingsById(booker.getId(), created.id());

        assertThat(result.lastBooking()).isNull();
        assertThat(result.nextBooking()).isNull();
    }

    @Test
    void getItemsWithBookingsByOwnerShouldReturnPaginated() {
        itemService.addItem(owner.getId(), createDto);
        ItemCreateDto otherDto = new ItemCreateDto("Other", "Other Desc", true, null);
        itemService.addItem(owner.getId(), otherDto);

        List<ItemWithBookingsDto> result = itemService.getItemsWithBookingsByOwner(owner.getId(), 0, 10);

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void searchItemsShouldReturnMatchingAvailable() {
        itemService.addItem(owner.getId(), createDto); // "Item"
        ItemCreateDto unavailable = new ItemCreateDto("Tool", "Desc", false, null);
        itemService.addItem(owner.getId(), unavailable);

        List<ItemDto> result = itemService.searchItems("item", 0, 10);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).name()).isEqualTo("Item");
    }

    @Test
    void searchItemsWhenBlankTextThenReturnEmpty() {
        List<ItemDto> result = itemService.searchItems("", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void addCommentShouldSaveAfterBookingEnd() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);
        BookingCreateDto bookingDto = new BookingCreateDto(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), created.id());
        BookingDto booking = bookingService.addBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), booking.id(), true);

        CommentCreateDto commentDto = new CommentCreateDto("Good item");
        CommentDto result = itemService.addComment(booker.getId(), created.id(), commentDto);

        assertThat(result.text()).isEqualTo("Good item");
        assertThat(result.authorName()).isEqualTo("Booker");
    }

    @Test
    void addCommentWhenNoBookingThenThrowValidation() {
        ItemDto created = itemService.addItem(owner.getId(), createDto);
        CommentCreateDto commentDto = new CommentCreateDto("Comment");

        assertThatThrownBy(() -> itemService.addComment(booker.getId(), created.id(), commentDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Пользователь не бронировал вещь или бронирование не завершено");
    }
}
