package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private ItemRepositoryJpa itemRepository;

    @Test
    void saveUser_shouldCreateUser() {
        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Иван");
        dto.setEmail("ivan@test.ru");

        UserResponseDto created = userService.saveUser(dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Иван", created.getName());
        assertEquals("ivan@test.ru", created.getEmail());

        User savedUser = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Иван", savedUser.getName());
        assertEquals("ivan@test.ru", savedUser.getEmail());
    }

    @Test
    void saveUser_withExistingEmail_shouldThrowException() {
        User existingUser = createUser("Иван", "ivan@test.ru");

        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Петр");
        dto.setEmail("ivan@test.ru");

        assertThrows(RuntimeException.class, () -> {
            userService.saveUser(dto);
        });
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        createUser("Иван", "ivan@test.ru");
        createUser("Петр", "petr@test.ru");

        List<UserResponseDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = createUser("Иван", "ivan@test.ru");

        UserResponseDto found = userService.getUserById(user.getId());

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals("Иван", found.getName());
        assertEquals("ivan@test.ru", found.getEmail());
    }

    @Test
    void getUserById_withNonExistentId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void getUserById_shouldReturnUserWithItems() {
        User user = createUser("Иван", "ivan@test.ru");
        createItem(user, "Дрель", true);
        createItem(user, "Перфоратор", true);

        UserResponseDto found = userService.getUserById(user.getId());

        assertNotNull(found);
        assertNotNull(found.getItemList());
        assertEquals(2, found.getItemList().size());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        User user = createUser("Иван", "ivan@test.ru");

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Иван Новый");
        dto.setEmail("ivan.new@test.ru");

        UserResponseDto updated = userService.updateUser(user.getId(), dto);

        assertNotNull(updated);
        assertEquals("Иван Новый", updated.getName());
        assertEquals("ivan.new@test.ru", updated.getEmail());

        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Иван Новый", savedUser.getName());
        assertEquals("ivan.new@test.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_withExistingEmail_shouldThrowException() {
        User user1 = createUser("Иван", "ivan@test.ru");
        User user2 = createUser("Петр", "petr@test.ru");

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setEmail("ivan@test.ru");

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(user2.getId(), dto);
        });
    }

    @Test
    void updateUser_withNonExistentId_shouldThrowException() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Новое имя");

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, dto);
        });
    }

    @Test
    void updateUser_partialUpdate_shouldWork() {
        User user = createUser("Иван", "ivan@test.ru");

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Иван Новый");

        UserResponseDto updated = userService.updateUser(user.getId(), dto);

        assertNotNull(updated);
        assertEquals("Иван Новый", updated.getName());
        assertEquals("ivan@test.ru", updated.getEmail());
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = createUser("Иван", "ivan@test.ru");

        userService.delete(user.getId());

        Optional<User> deleted = userRepository.findById(user.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void delete_withNonExistentId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            userService.delete(999L);
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
}