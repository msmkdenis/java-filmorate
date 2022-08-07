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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private String userJsonString;
    private User user;
    private Gson gson;


    @BeforeEach
    public void beforeEach() {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

        user = new User(1, "some@email", "login", "Danil", LocalDate.of(1990,01,01));
    }

    @Test
    @DisplayName("Регистрация пользователя")
    public void mustCreateUserSuccessfully() throws Exception {
        user = new User(1, "some@email", "login", "Danil", LocalDate.of(1990,01,01));
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Danil"));
    }

    @Test
    @DisplayName("Пользователь с логином с пробелами не должен быть зарегистрирован")
    public void mustReturn400onSpaceLogin() throws Exception {
        user.setLogin("dolore ullamco");
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Пользователь с пустым логином не должен быть зарегистрирован")
    public void mustReturn400onEmptyLogin() throws Exception {
        user.setLogin("");
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Пользователь с пустым именем должен быть зарегистрирован под своим логином")
    public void mustReturn400onEmptyName() throws Exception {
        user.setName("");
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("login"));
    }

    @Test
    @DisplayName("Пользователь с датой рождения в будущем не должен быть зарегистрирован")
    public void mustReturn400onFutureBirthday() throws Exception {
        LocalDate birthday = LocalDate.of(2050,01,01);
        user.setBirthday(birthday);
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Пользователь с некорректным Id не должен быть зарегистрирован")
    public void mustReturn400onWrongId() throws Exception {
        user.setId(-1);
        userJsonString = gson.toJson(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .content(userJsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
