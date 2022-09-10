package ru.practicum.contract.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @Email(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class})
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String email;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    @Size(min = 2, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class})
    private String name;
}
