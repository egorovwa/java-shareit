package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Min(value = 1,message = "ID меньше 1")
    private Long id;
    @Email(message = "Не верный формат Email")
    private String email;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
}
