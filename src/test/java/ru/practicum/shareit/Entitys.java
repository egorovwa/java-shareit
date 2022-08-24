package ru.practicum.shareit;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Entitys {
    public static User USER_ID1 = new User(1L,"Mail@mail.ru","userName");
    public static User USER_ID2 = new User(2L,"Mail2@mail.ru","userName2");
    public static LocalDateTime TEST_TIME_DATE_TIME = LocalDateTime.of(2022,8,17,
            6,0,0);
    public static Long TEST_TIME_LONG =  TEST_TIME_DATE_TIME.toEpochSecond(ZoneOffset.UTC);


}
