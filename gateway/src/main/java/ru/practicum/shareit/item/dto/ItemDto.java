package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @Size(groups = {Marker.OnCreate.class, Marker.OnPatch.class}, min = 3)
    @NotNull(groups = Marker.OnCreate.class)
    private String name;
    @Size(groups = {Marker.OnCreate.class, Marker.OnPatch.class},min = 3)
    @NotNull(groups = Marker.OnCreate.class)
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
