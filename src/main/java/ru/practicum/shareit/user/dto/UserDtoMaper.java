package ru.practicum.shareit.user.dto;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Email;

@Component
public class UserDtoMaper {
    public UserDto toDto(User user){
     return new UserDto(user.getId(), user.getEmail(), user.getName()) ;
    }
    public User fromDto(UserDto dto){
        return new User(dto.getId(), dto.getEmail(), dto.getName());
    }

}
