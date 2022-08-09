package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controllers.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private final String LONG_DESCRIPTION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Autowired
    private MockMvc mockMvc;
    private String filmJsonString;
    private Film film;
    private Gson gson;


    @BeforeEach
    public void beforeEach() {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

        film = new Film(1, "Фильм 1", "Описание фильма 1", LocalDate.of(2022, 5, 10), 30);
    }

    @Test
    @DisplayName("Создание фильма")
    public void mustCreateFilmSuccessfully() throws Exception {
        film = new Film(1, "Фильм 1", "Описание фильма 1", LocalDate.of(2022, 5, 10), 30);
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Фильм 1"));
    }

    @Test
    @DisplayName("Фильм с пустым именем не должен создаваться")
    public void mustReturn400onEmptyName() throws Exception {
        film.setName("");
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм с кол-вом символом в названии > 200 не должен создаваться")
    public void mustReturn400onLongName() throws Exception {
        film.setDescription(LONG_DESCRIPTION);
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм с датой релиза до 28.12.1895 не должен создаваться")
    public void mustReturn400onEarlyRelease() throws Exception {
        LocalDate date = LocalDate.of(1800,1,1);
        film.setReleaseDate(date);
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм с отрицательной длительностью не должен создаваться")
    public void mustReturn400onNegativeDuration() throws Exception {
        film.setDuration(-1);
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм с некорректным Id не должен обновляться")
    public void mustReturn400onWrongIdUpdate() throws Exception {
        film.setId(-1);
        filmJsonString = gson.toJson(film);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .content(filmJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
