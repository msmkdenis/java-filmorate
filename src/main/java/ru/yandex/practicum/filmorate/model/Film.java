package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.controllers.validators.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film extends BaseEntity {

    @NotBlank(message = "Название фильма не может быть пустым")
    @NotNull(message = "Отсутствует название фильма")
    private String name;

    @Size(max = 200, message = "Максимальное кол-во символов - 200")
    @NotNull(message = "Отсутствует описание фильма")
    private String description;

    @ReleaseDateConstraint
    @NotNull(message = "Отсутствует дата выхода фильма")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @NotNull(message = "Отсутствует рейтинг фильма.")
    private Mpa mpa;

    private Set<Genre> genres;

    private int rate;
    private List<Director> directors;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        super(id);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
