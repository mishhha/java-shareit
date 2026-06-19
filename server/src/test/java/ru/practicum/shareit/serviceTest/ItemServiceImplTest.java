package ru.practicum.shareit.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.dto.item.CommentRequestDto;
import ru.practicum.shareit.dto.item.CommentResponseDto;
import ru.practicum.shareit.dto.item.ItemResponseDto;
import ru.practicum.shareit.dto.item.NewItemRequestDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.BookingRepositoryJpa;
import ru.practicum.shareit.repository.CommentRepositoryJpa;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.repository.UserRepositoryJpa;
import ru.practicum.shareit.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepositoryJpa itemRepository;

    @Mock
    private UserRepositoryJpa userRepository;

    @Mock
    private BookingRepositoryJpa bookingRepository;

    @Mock
    private CommentRepositoryJpa commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void searchItemsByText_shouldReturnMatchingItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");

        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(1L);
        dto.setName("Дрель");

        when(itemRepository.findItemByText("%дрель%")).thenReturn(List.of(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(dto);

        List<ItemResponseDto> result = itemService.searchItemsByText("дрель");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchItemsByText_withEmptyText_shouldReturnEmptyList() {
        List<ItemResponseDto> result = itemService.searchItemsByText("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void create_shouldCreateNewItem() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");

        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setName("Дрель");

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(dto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(responseDto);

        ItemResponseDto result = itemService.create(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).save(item);
    }

    @Test
    void create_withNonExistentUser_shouldThrowException() {
        NewItemRequestDto dto = new NewItemRequestDto();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.create(999L, dto);
        });
    }

    @Test
    void findById_shouldReturnItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");

        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(dto);

        ItemResponseDto result = itemService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_withNonExistentId_shouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.findById(999L);
        });
    }

    @Test
    void update_shouldUpdateItem() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setUser(owner);

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Обновлённая дрель");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(responseDto);

        ru.practicum.shareit.dto.item.UpdateItemRequestDto updateDto =
            new ru.practicum.shareit.dto.item.UpdateItemRequestDto();
        updateDto.setName("Обновлённая дрель");

        ItemResponseDto result = itemService.update(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("Обновлённая дрель", result.getName());
    }

    @Test
    void update_withNonOwner_shouldThrowException() {
        User owner = new User();
        owner.setId(1L);

        User other = new User();
        other.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setUser(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ru.practicum.shareit.dto.item.UpdateItemRequestDto updateDto =
            new ru.practicum.shareit.dto.item.UpdateItemRequestDto();

        assertThrows(RuntimeException.class, () -> {
            itemService.update(1L, 2L, updateDto);
        });
    }

    @Test
    void delete_shouldRemoveItem() {
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(1L);

        itemService.delete(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void delete_withNonExistentId_shouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.delete(999L);
        });
    }

    @Test
    void createComment_shouldCreateComment() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setUser(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отлично!");

        CommentResponseDto responseDto = new CommentResponseDto();
        responseDto.setId(1L);

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Отлично!");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsApprovedCompletedBooking(eq(1L), eq(1L), any(LocalDateTime.class)))
            .thenReturn(true);
        when(itemMapper.mapToComment(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(itemMapper.mapToCommentDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = itemService.createComment(1L, 1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void createComment_withoutRental_shouldThrowException() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Отлично!");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsApprovedCompletedBooking(eq(1L), eq(1L), any(LocalDateTime.class)))
            .thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            itemService.createComment(1L, 1L, dto);
        });
    }
}