package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageParamTest {

    @Test
    void create_normal() throws IncorrectPageValueException {
        PageParam pageParam = PageParam.create(3, 2);
        assertThat(pageParam.getPage(), is(1));
        assertThat(pageParam.size, is(2));
    }

    @Test
    void create_negativeSize() {
        assertThrows(IncorrectPageValueException.class, () -> {
            PageParam pageParam = PageParam.create(0, -1);
        });
    }

    @Test
    void create_negativeFrom() {
        assertThrows(IncorrectPageValueException.class, () -> {
            PageParam pageParam = PageParam.create(-1, 1);
        });
    }
}