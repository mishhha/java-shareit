package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.repository.ItemRepositoryJpa;
import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.UserRepositoryJpa;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryJpa repository;
    private final ItemRepositoryJpa itemRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public UserResponseDto saveUser(NewUserRequestDto newUserRequestDto) {

        if (repository.existsUserByEmail(newUserRequestDto.getEmail())) {
            throw new ConflictException("Пользователь с email " +
                newUserRequestDto.getEmail() +
                " уже зарегистрирован.");
        }

        User user = userMapper.mapToUser(newUserRequestDto);

        User createUser = repository.save(user);

        return userMapper.mapToUserDto(createUser);

    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return repository.findAll().stream()
            .map(userMapper::mapToUserDto)
            .toList();
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        Optional<User> user = repository.findById(userId);

        List<Item> items = itemRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        UserResponseDto dto = userMapper.mapToUserDto(user.get());
        dto.setItemList(items.stream().map(itemMapper::mapToItemDto).toList());

        return dto;
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {

        Optional<User> oldUser = repository.findById(userId);
        if (oldUser.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User user = oldUser.get();

        if (updateUserRequestDto.getEmail() != null &&
            !updateUserRequestDto.getEmail().equalsIgnoreCase(user.getEmail())
        ) {
            boolean find = repository.existsUserByEmail(updateUserRequestDto.getEmail());
            if (find) {
                throw new ConflictException("Пользователь с email " +
                    updateUserRequestDto.getEmail() +
                    " уже зарегистрирован."
                );
            }
        }

        if (updateUserRequestDto.getName() != null) {
            user.setName(updateUserRequestDto.getName());
        }

        if (updateUserRequestDto.getEmail() != null) {
            user.setEmail(updateUserRequestDto.getEmail());
        }

        User updateUser = repository.save(user);

        return userMapper.mapToUserDto(updateUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        repository.deleteById(id);
    }

}
