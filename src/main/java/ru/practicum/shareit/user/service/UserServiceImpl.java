package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public UserResponseDto saveUser(NewUserRequestDto newUserRequestDto) {

        if (checkUserByEmail(newUserRequestDto.getEmail())) {
            throw new ConflictException(
                "Пользователь с EMAIL " + newUserRequestDto.getEmail() + " уже существует."
            );
        }

        User user = userMapper.mapToUser(newUserRequestDto);

        User createUser = repository.save(user);

        return userMapper.mapToUserDto(createUser);
    }

    @Override
    public boolean checkUserByEmail(String email) {
        return repository.checkUserByEmail(email);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return repository.findAll().stream()
            .map(userMapper::mapToUserDto)
            .toList();
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = repository.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        List<Item> items = itemRepository.findItemsByOwnerId(userId);

        UserResponseDto dto = userMapper.mapToUserDto(user);
        dto.setItemList(items.stream().map(itemMapper::mapToItemDto).toList());

        return dto;
    }

    @Override
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {

        User oldUser = repository.findById(userId);
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }

        if (updateUserRequestDto.getEmail() != null && !updateUserRequestDto.getEmail().equals(oldUser.getEmail())) {
            if(checkUserByEmail(updateUserRequestDto.getEmail())) {
                throw new ConflictException(
                    "Пользователь с EMAIL " + updateUserRequestDto.getEmail() + " уже существует."
                );
            }
        }

        if (updateUserRequestDto.getName() != null) {
            oldUser.setName(updateUserRequestDto.getName());
        }

        if (updateUserRequestDto.getEmail() != null) {
            oldUser.setEmail(updateUserRequestDto.getEmail());
        }

        User updateUser = repository.update(oldUser);

        return userMapper.mapToUserDto(updateUser);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

}
