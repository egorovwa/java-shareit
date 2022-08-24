package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor")
    private User requestor;
    private Long created;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "request")
    private Collection<Item> items = new ArrayList<>();

    public ItemRequest(String description, User requestor) {
        this.description = description;
        this.requestor = requestor;
        this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
