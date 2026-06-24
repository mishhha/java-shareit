package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.dto.request.ItemRequestBodyDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.model.ItemRequest;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.ItemRequestRepository;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Test
    void save_shouldCreateItemRequest() {
        User user = createUser("Иван", "ivan@test.ru");

        ItemRequestBodyDto dto = new ItemRequestBodyDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto response = itemRequestService.save(dto, user.getId());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Нужна дрель", response.getDescription());
        assertEquals(user.getId(), response.getRequesterId());

        ItemRequest savedRequest = itemRequestRepository.findById(response.getId()).orElseThrow();
        assertEquals("Нужна дрель", savedRequest.getDescription());
        assertEquals(user.getId(), savedRequest.getRequester().getId());
    }

    @Test
    void save_withNonExistentUser_shouldThrowException() {
        ItemRequestBodyDto dto = new ItemRequestBodyDto();
        dto.setDescription("Нужна дрель");

        assertThrows(RuntimeException.class, () -> {
            itemRequestService.save(dto, 999L);
        });
    }

    @Test
    void findAllYourRequestsById_shouldReturnUserRequests() {
        User user = createUser("Иван", "ivan@test.ru");

        createItemRequest(user, "Нужна дрель");
        createItemRequest(user, "Нужен перфоратор");

        List<ItemRequestDto> requests = itemRequestService.findAllYourRequestsById(user.getId());

        assertNotNull(requests);
        assertEquals(2, requests.size());
    }

    @Test
    void findAllYourRequestsById_withNonExistentUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            itemRequestService.findAllYourRequestsById(999L);
        });
    }

    @Test
    void findAllRequests_shouldReturnAllOtherRequests() {
        User user1 = createUser("Иван", "ivan@test.ru");
        User user2 = createUser("Петр", "petr@test.ru");

        createItemRequest(user1, "Нужна дрель");
        createItemRequest(user2, "Нужен молоток");

        List<ItemRequestDto> requests = itemRequestService.findAllRequests(user1.getId());

        assertNotNull(requests);
        assertTrue(requests.size() >= 1);
    }

    @Test
    void findItemRequestById_shouldReturnRequest() {
        User user = createUser("Иван", "ivan@test.ru");
        ItemRequest request = createItemRequest(user, "Нужна дрель");

        ItemRequestDto response = itemRequestService.findItemRequestById(request.getId());

        assertNotNull(response);
        assertEquals(request.getId(), response.getId());
        assertEquals("Нужна дрель", response.getDescription());
    }

    @Test
    void findItemRequestById_withNonExistentId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            itemRequestService.findItemRequestById(999L);
        });
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepositoryJpa.save(user);
    }

    private ItemRequest createItemRequest(User user, String description) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(user);
        request.setDateTime(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }
}