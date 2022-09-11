package ru.practicum.contract.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.ValidationMarker;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class}, min = 3)
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String name;
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnPatch.class}, min = 3)
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String description;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean available;
    private Long requestId;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
