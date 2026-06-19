package ru.practicum.shareit.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.user.UserResponseDto;
import ru.practicum.shareit.dto.user.NewUserRequestDto;
import ru.practicum.shareit.model.User;

@Component
@NoArgsConstructor
public class UserMapper {

    public User mapToUser(NewUserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        return user;
    }

    public UserResponseDto mapToUserDto(User user) {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

}
