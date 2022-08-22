package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorageDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorageDao directorStorageDao;

    public Collection<Director> getAll() {
        return directorStorageDao.findAll();
    }

    public Director getById(long id) {
        return directorStorageDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Режиссер с id = %s не найден.", id)));
    }

    public Director create(Director director) {
        return directorStorageDao.add(director).get();
    }

    public Director update(Director director) {
        getById(director.getId());
        return directorStorageDao.update(director).get();
    }

    public void delete(long id) {
        getById(id);
        directorStorageDao.deleteById(id);
    }
}
