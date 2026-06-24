package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.dto.item.CommentRequestDto;
import ru.practicum.shareit.dto.item.CommentResponseDto;
import ru.practicum.shareit.dto.item.ItemResponseDto;
import ru.practicum.shareit.dto.item.NewItemRequestDto;
import ru.practicum.shareit.dto.item.UpdateItemRequestDto;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.repository.CommentRepositoryJpa;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepositoryJpa itemRepository;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private BookingRepositoryJpa bookingRepository;

    @Autowired
    private CommentRepositoryJpa commentRepository;

    @Test
    void searchItemsByText_shouldReturnMatchingItems() {
        User user = createUser("Иван", "ivan@test.ru");
        createItem(user, "Дрель электрическая", true);
        createItem(user, "Перфоратор", true);

        List<ItemResponseDto> results = itemService.searchItemsByText("дрель");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getName().toLowerCase().contains("дрель"));
    }

    @Test
    void searchItemsByText_withEmptyText_shouldReturnEmptyList() {
        List<ItemResponseDto> results = itemService.searchItemsByText("");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void searchItemsByText_withNullText_shouldReturnEmptyList() {
        List<ItemResponseDto> results = itemService.searchItemsByText(null);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void userItems_shouldReturnItemsWithBookingsAndComments() {
        User owner = createUser("Владелец", "owner@test.ru");
        User booker = createUser("Бронировщик", "booker@test.ru");
        Item item = createItem(owner, "Дрель", true);

        createBooking(booker, item, BookingStatus.APPROVED, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1));
        createBooking(booker, item, BookingStatus.WAITING, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5));
        createComment(booker, item, "Отличная вещь!");

        List<ItemResponseDto> items = itemService.userItems(owner.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getLastBooking());
        assertNotNull(items.get(0).getNextBooking());
        assertEquals(1, items.get(0).getComments().size());
    }

    @Test
    void userItems_withNonExistentUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            itemService.userItems(999L);
        });
    }

    @Test
    void create_shouldCreateNewItem() {
        User owner = createUser("Иван", "ivan@test.ru");

        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        ItemResponseDto created = itemService.create(owner.getId(), dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Дрель", created.getName());
        assertEquals("Мощная дрель", created.getDescription());
        assertTrue(created.getAvailable());

        Item savedItem = itemRepository.findById(created.getId()).orElseThrow();
        assertEquals(owner.getId(), savedItem.getUser().getId());
    }

    @Test
    void create_withNonExistentUser_shouldThrowException() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setName("Дрель");

        assertThrows(RuntimeException.class, () -> {
            itemService.create(999L, dto);
        });
    }

    @Test
    void findAll_shouldReturnAllItems() {
        User user = createUser("Иван", "ivan@test.ru");
        createItem(user, "Дрель", true);
        createItem(user, "Перфоратор", true);

        List<ItemResponseDto> items = itemService.findAll();

        assertNotNull(items);
        assertTrue(items.size() >= 2);
    }

    @Test
    void findById_shouldReturnItem() {
        User user = createUser("Иван", "ivan@test.ru");
        Item item = createItem(user, "Дрель", true);

        ItemResponseDto found = itemService.findById(item.getId());

        assertNotNull(found);
        assertEquals(item.getId(), found.getId());
        assertEquals("Дрель", found.getName());
    }

    @Test
    void findById_withNonExistentId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            itemService.findById(999L);
        });
    }

    @Test
    void update_shouldUpdateItem() {
        User owner = createUser("Иван", "ivan@test.ru");
        Item item = createItem(owner, "Дрель", true);

        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setName("Обновлённая дрель");
        dto.setDescription("Новое описание");
        dto.setAvailable(false);

        ItemResponseDto updated = itemService.update(item.getId(), owner.getId(), dto);

        assertNotNull(updated);
        assertEquals("Обновлённая дрель", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void update_withNonOwner_shouldThrowException() {
        User owner = createUser("Владелец", "owner@test.ru");
        User other = createUser("Другой", "other@test.ru");
        Item item = createItem(owner, "Дрель", true);

        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setName("Новое имя");

        assertThrows(RuntimeException.class, () -> {
            itemService.update(item.getId(), other.getId(), dto);
        });
    }

    @Test
    void delete_shouldRemoveItem() {
        User user = createUser("Иван", "ivan@test.ru");
        Item item = createItem(user, "Дрель", true);

        itemService.delete(item.getId());

        Optional<Item> deleted = itemRepository.findById(item.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void delete_withNonExistentId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            itemService.delete(999L);
        });
    }

    @Test
    void createComment_shouldCreateComment() {
        User owner = createUser("Владелец", "owner@test.ru");
        User booker = createUser("Бронировщик", "booker@test.ru");
        Item item = createItem(owner, "Дрель", true);

        createBooking(booker, item, BookingStatus.APPROVED, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1));

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Отличная вещь!");

        CommentResponseDto created = itemService.createComment(item.getId(), booker.getId(), dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Отличная вещь!", created.getText());
        assertEquals(booker.getName(), created.getAuthorName());

        Comment savedComment = commentRepository.findById(created.getId()).orElseThrow();
        assertEquals(item.getId(), savedComment.getItem().getId());
    }

    @Test
    void createComment_withoutRental_shouldThrowException() {
        User owner = createUser("Владелец", "owner@test.ru");
        User user = createUser("Пользователь", "user@test.ru");
        Item item = createItem(owner, "Дрель", true);

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Отличная вещь!");

        assertThrows(RuntimeException.class, () -> {
            itemService.createComment(item.getId(), user.getId(), dto);
        });
    }

    @Test
    void createComment_withNonExistentItem_shouldThrowException() {
        User user = createUser("Пользователь", "user@test.ru");

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Отличная вещь!");

        assertThrows(RuntimeException.class, () -> {
            itemService.createComment(999L, user.getId(), dto);
        });
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, Boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Описание");
        item.setAvailable(available);
        item.setUser(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, BookingStatus status, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return bookingRepository.save(booking);
    }

    private Comment createComment(User user, Item item, String text) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setItem(item);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}