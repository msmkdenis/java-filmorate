package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorageDao genreStorageDao;

    public List<Genre> findAll() {
        return genreStorageDao.findAll();
    }

    public Genre findById(long id) {
        return genreStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Жанр с id = %s не найден", id)));
    }
}
