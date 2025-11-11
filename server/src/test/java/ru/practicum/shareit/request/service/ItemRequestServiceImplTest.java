package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@email.com");
        user = userRepository.save(user);
        otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@email.com");
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void addRequestShouldSaveAndReturnDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Need a tool");

        ItemRequestDto result = itemRequestService.addRequest(user.getId(), dto);

        assertThat(result.id()).isNotNull();
        assertThat(result.description()).isEqualTo("Need a tool");
        assertThat(result.created()).isNotNull();
        assertThat(result.items()).isEmpty();
    }

    @Test
    void addRequestWhenInvalidUserThenThrowNotFound() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Need a tool");

        assertThatThrownBy(() -> itemRequestService.addRequest(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getRequestsByUserShouldReturnEmptyForNoRequests() {
        List<ItemRequestDto> result = itemRequestService.getRequestsByUser(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getRequestsByUserShouldReturnSortedWithItems() {
        ItemRequest req1 = new ItemRequest();
        req1.setDescription("Req1");
        req1.setRequestor(user);
        req1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(req1);
        ItemRequest req2 = new ItemRequest();
        req2.setDescription("Req2");
        req2.setRequestor(user);
        req2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(req2);

        ItemCreateDto itemDto = new ItemCreateDto("Item", "Desc", true, req1.getId());
        itemService.addItem(user.getId(), itemDto);

        List<ItemRequestDto> result = itemRequestService.getRequestsByUser(user.getId());

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).description()).isEqualTo("Req2"); // Newer first
        assertThat(result.get(1).items().size()).isEqualTo(1);
    }

    @Test
    void getAllRequestsShouldReturnPaginatedExcludingOwn() {
        ItemRequest ownReq = new ItemRequest();
        ownReq.setDescription("Own");
        ownReq.setRequestor(user);
        ownReq.setCreated(LocalDateTime.now());
        itemRequestRepository.save(ownReq);
        ItemRequest otherReq1 = new ItemRequest();
        otherReq1.setDescription("Other1");
        otherReq1.setRequestor(otherUser);
        otherReq1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(otherReq1);
        ItemRequest otherReq2 = new ItemRequest();
        otherReq2.setDescription("Other2");
        otherReq2.setRequestor(otherUser);
        otherReq2.setCreated(LocalDateTime.now().minusDays(2));
        itemRequestRepository.save(otherReq2);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId(), 0, 1);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).description()).isEqualTo("Other1"); // Newer first, excluding own
    }

    @Test
    void getAllRequestsWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> itemRequestService.getAllRequests(999L, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getRequestByIdShouldReturnWithItems() {
        ItemRequest req = new ItemRequest();
        req.setDescription("Req");
        req.setRequestor(user);
        req.setCreated(LocalDateTime.now());
        req = itemRequestRepository.save(req);

        // Add item
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(req);
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getRequestById(otherUser.getId(), req.getId());

        assertThat(result.description()).isEqualTo("Req");
        assertThat(result.items().size()).isEqualTo(1);
        assertThat(result.items().get(0).name()).isEqualTo("Item");
    }

    @Test
    void getRequestByIdWhenInvalidRequestThenThrowNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос не найден");
    }

    @Test
    void getRequestByIdWhenInvalidUserThenThrowNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }
}