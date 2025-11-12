package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemService itemService;

    private User owner;
    private User booker;
    private Long itemId;
    private BookingCreateDto createDto;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Owner").email("owner@email.com").build());
        booker = userRepository.save(User.builder().name("Booker").email("booker@email.com").build());
        ItemCreateDto itemDto = new ItemCreateDto("Item", "Desc", true, null);
        itemId = itemService.addItem(owner.getId(), itemDto).id();
        createDto = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId);
    }

    @Test
    void addBookingShouldSaveWithWaitingStatus() {
        BookingDto result = bookingService.addBooking(booker.getId(), createDto);

        assertThat(result.status()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.booker().id()).isEqualTo(booker.getId());
    }

    @Test
    void addBookingWhenOwnerBooksOwnItemThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.addBooking(owner.getId(), createDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Владелец не может бронировать свою вещь");
    }

    @Test
    void addBookingWhenItemUnavailableThenThrowValidation() {
        ItemCreateDto unavailable = new ItemCreateDto("Unavailable", "Desc", false, null);
        Long unavailId = itemService.addItem(owner.getId(), unavailable).id();
        BookingCreateDto badDto = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), unavailId);

        assertThatThrownBy(() -> bookingService.addBooking(booker.getId(), badDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Вещь недоступна");
    }

    @Test
    void addBookingWhenEndBeforeStartThenThrowValidation() {
        BookingCreateDto badDto = new BookingCreateDto(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1), itemId);

        assertThatThrownBy(() -> bookingService.addBooking(booker.getId(), badDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Дата окончания раньше начала");
    }

    @Test
    void addBookingWhenInvalidItemThenThrowNotFound() {
        BookingCreateDto badDto = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 999L);

        assertThatThrownBy(() -> bookingService.addBooking(booker.getId(), badDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь не найдена");
    }

    @Test
    void addBookingWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.addBooking(999L, createDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void approveBookingShouldChangeStatusToApproved() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);

        BookingDto result = bookingService.approveBooking(owner.getId(), created.id(), true);

        assertThat(result.status()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBookingShouldChangeStatusToRejected() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);

        BookingDto result = bookingService.approveBooking(owner.getId(), created.id(), false);

        assertThat(result.status()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBookingWhenNotOwnerThenThrowAccessDenied() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), created.id(), true))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Только владелец может подтверждать");
    }

    @Test
    void approveBookingWhenAlreadyApprovedThenThrowConflict() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);
        bookingService.approveBooking(owner.getId(), created.id(), true);

        assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), created.id(), true))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Статус уже изменен");
    }

    @Test
    void approveBookingWhenInvalidBookingThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), 999L, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование не найдено");
    }

    @Test
    void getBookingByIdShouldReturnForBooker() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);

        BookingDto result = bookingService.getBookingById(booker.getId(), created.id());

        assertThat(result.id()).isEqualTo(created.id());
    }

    @Test
    void getBookingByIdShouldReturnForOwner() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);

        BookingDto result = bookingService.getBookingById(owner.getId(), created.id());

        assertThat(result.id()).isEqualTo(created.id());
    }

    @Test
    void getBookingByIdWhenUnauthorizedUserThenThrowNotFound() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);
        User stranger = userRepository.save(User.builder().name("Stranger").email("stranger@email.com").build());

        assertThatThrownBy(() -> bookingService.getBookingById(stranger.getId(), created.id()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Доступ запрещен");
    }

    @Test
    void getBookingByIdWhenInvalidBookingThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.getBookingById(booker.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование не найдено");
    }

    @Test
    void getBookingsByUserWhenAllStateThenReturnList() {
        bookingService.addBooking(booker.getId(), createDto);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByUserWhenCurrentStateThenFilter() {
        BookingCreateDto currentDto = new BookingCreateDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), itemId);
        bookingService.addBooking(booker.getId(), currentDto);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "CURRENT", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByUserWhenPastStateThenFilter() {
        BookingCreateDto pastDto = new BookingCreateDto(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), itemId);
        bookingService.addBooking(booker.getId(), pastDto);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "PAST", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByUserWhenFutureStateThenFilter() {
        BookingCreateDto futureDto = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId);
        bookingService.addBooking(booker.getId(), futureDto);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "FUTURE", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByUserWhenWaitingStateThenFilter() {
        bookingService.addBooking(booker.getId(), createDto);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "WAITING", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByUserWhenRejectedStateThenFilter() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);
        bookingService.approveBooking(owner.getId(), created.id(), false);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "REJECTED", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByUserWhenInvalidStateThenThrowValidation() {
        assertThatThrownBy(() -> bookingService.getBookingsByUser(booker.getId(), "INVALID", 0, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неизвестное состояние");
    }

    @Test
    void getBookingsByUserWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.getBookingsByUser(999L, "ALL", 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getBookingsByUserWhenPaginatedThenReturnSubset() {
        for (int i = 0; i < 5; i++) {
            BookingCreateDto dto = new BookingCreateDto(LocalDateTime.now().plusDays(i + 1), LocalDateTime.now().plusDays(i + 2), itemId);
            bookingService.addBooking(booker.getId(), dto);
        }

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "ALL", 0, 2);

        assertThat(result).hasSize(2);
    }

    @Test
    void getBookingsByOwnerWhenAllStateThenReturnList() {
        bookingService.addBooking(booker.getId(), createDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "ALL", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByOwnerWhenCurrentStateThenFilter() {
        BookingCreateDto currentDto = new BookingCreateDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), itemId);
        bookingService.addBooking(booker.getId(), currentDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "CURRENT", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByOwnerWhenPastStateThenFilter() {
        BookingCreateDto pastDto = new BookingCreateDto(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), itemId);
        bookingService.addBooking(booker.getId(), pastDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "PAST", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByOwnerWhenFutureStateThenFilter() {
        BookingCreateDto futureDto = new BookingCreateDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId);
        bookingService.addBooking(booker.getId(), futureDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "FUTURE", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByOwnerWhenWaitingStateThenFilter() {
        bookingService.addBooking(booker.getId(), createDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "WAITING", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByOwnerWhenRejectedStateThenFilter() {
        BookingDto created = bookingService.addBooking(booker.getId(), createDto);
        bookingService.approveBooking(owner.getId(), created.id(), false);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "REJECTED", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByOwnerWhenInvalidStateThenThrowValidation() {
        assertThatThrownBy(() -> bookingService.getBookingsByOwner(owner.getId(), "INVALID", 0, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Неизвестное состояние");
    }

    @Test
    void getBookingsByOwnerWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> bookingService.getBookingsByOwner(999L, "ALL", 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getBookingsByOwnerWhenPaginatedThenReturnSubset() {
        for (int i = 0; i < 5; i++) {
            BookingCreateDto dto = new BookingCreateDto(LocalDateTime.now().plusDays(i + 1), LocalDateTime.now().plusDays(i + 2), itemId);
            bookingService.addBooking(booker.getId(), dto);
        }

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "ALL", 0, 2);

        assertThat(result).hasSize(2);
    }
}
