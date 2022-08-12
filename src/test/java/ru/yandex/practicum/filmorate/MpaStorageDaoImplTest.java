package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IncorrectMpaIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaStorageDaoImplTest {

    private final MpaService mpaService;

    @Autowired
    public MpaStorageDaoImplTest(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @Test
    @DisplayName("Найти mpa по его id")
    void findByIdTest() {
        Mpa mpa = mpaService.findMpaById(1);
        assertEquals("G", mpa.getName());
        assertThrows(IncorrectMpaIdException.class, () -> mpaService.findMpaById(-1));
    }
}
