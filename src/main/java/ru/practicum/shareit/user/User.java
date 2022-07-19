package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Data
public class User {
    @Min(value = 1,message = "ID меньше 1")
    private Long id;
    @Email(message = "Не верный формат Email")
    private String email;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
}
