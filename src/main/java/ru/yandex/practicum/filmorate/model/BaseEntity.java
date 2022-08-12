package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {
    protected Long id;

    public BaseEntity() {
    }

    public BaseEntity(Long id) {
        this.id = id;
    }
}