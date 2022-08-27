package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Director extends BaseEntity {
    @NotBlank(message = "Имя режиссера не может быть пустым.")
    @NotNull(message = "Отсутствует имя режиссера.")
    private String name;

    public Director(long id, String name) {
        super(id);
        this.name = name;
    }
}