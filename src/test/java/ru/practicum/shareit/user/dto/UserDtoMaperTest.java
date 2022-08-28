package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.Entitys.USER_ID1;


class UserDtoMaperTest {
    UserDtoMaper userDtoMaper = new UserDtoMaper();

    @Test
    void toDto() {
        User user = USER_ID1;
        UserDto resultDto = new UserDto(user.getId(), user.getEmail(), user.getName());
        assertThat(userDtoMaper.toDto(user), is(resultDto));

    }

    @Test
    void fromDto() {
        UserDto userDto = new UserDto(USER_ID1.getId(), USER_ID1.getEmail(), USER_ID1.getName());
        assertThat(userDtoMaper.fromDto(userDto), is(USER_ID1));
    }
}