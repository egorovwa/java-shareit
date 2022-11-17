package ru.practicum.contract.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.ValidationMarker;
import ru.practicum.contract.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoCreated {       // TODO: 09.09.2022 подумать за названин
    private Long id;
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class}, min = 3)
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String name;
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class}, min = 3)
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String description;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}

