package ru.practicum.shareit.util;

import lombok.EqualsAndHashCode;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;

@EqualsAndHashCode
public class PageParam {
    Integer page;
    Integer size;

    private PageParam(Integer from, Integer size) {
        this.page = from / size;
        this.size = size;
    }

    public static PageParam create(Integer from, Integer size) throws IncorrectPageValueException {
        if (from == null || size == null) {
            return null;
        } else {
            if (from < 0 || size <= 0) {
                throw new IncorrectPageValueException("Значения недолжныбыть менше 0");
            }
            return new PageParam(from, size);
        }
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

}
