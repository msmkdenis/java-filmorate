package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorageDao genreStorageDao;

    public GenreService(GenreStorageDao genreStorageDao) {
        this.genreStorageDao = genreStorageDao;
    }

    public List<Genre> findAll() {
        return genreStorageDao.findAll();
    }

    public Genre findById(Long id) {
        return genreStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Жанр с id = %s не найден", id)));
    }
}
