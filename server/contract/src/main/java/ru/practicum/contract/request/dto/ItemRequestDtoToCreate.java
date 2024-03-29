package ru.practicum.contract.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoToCreate {
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    private LocalDateTime created;
}
