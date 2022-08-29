package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageParamTest {

    @Test
    void test1_1createPageable_withOutSort() throws IncorrectPageValueException {
        assertThat(PageParam.createPageable(0, 1), is(PageRequest.of(0, 1)));
    }

    @Test
    void test1_2createPageable_withFromNull() throws IncorrectPageValueException {
        assertNull(PageParam.createPageable(null, 1));
    }

    @Test
    void test1_3createPageable_withSizeNull() throws IncorrectPageValueException {
        assertNull(PageParam.createPageable(null, 1));
    }

    @Test
    void test1_4createPageable_fromNegative() {
        assertThrows(IncorrectPageValueException.class, () -> PageParam.createPageable(-1, 1));
    }

    @Test
    void test1_5createPageable_sizeNegative() {
        assertThrows(IncorrectPageValueException.class, () -> PageParam.createPageable(0, -1));
    }

    @Test
    void test2_1createPageable_withSort() throws IncorrectPageValueException {
        assertThat(PageParam.createPageable(0, 1, "column"),
                is(PageRequest.of(0, 1, Sort.by("column").descending())));
    }

    @Test
    void test2_2createPageable_withFromNull() throws IncorrectPageValueException {
        assertNull(PageParam.createPageable(null, 1, "column"));
    }

    @Test
    void test2_3createPageable_withSizeNull() throws IncorrectPageValueException {
        assertNull(PageParam.createPageable(null, 1, "column"));
    }

    @Test
    void test2_4createPageable_fromNegative() {
        assertThrows(IncorrectPageValueException.class, () -> PageParam.createPageable(-1, 1));
    }

    @Test
    void test2_5createPageable_sizeNegative() {
        assertThrows(IncorrectPageValueException.class, () -> PageParam.createPageable(0, -1));
    }

}