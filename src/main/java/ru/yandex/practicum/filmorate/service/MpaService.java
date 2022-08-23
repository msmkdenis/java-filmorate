package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaStorageDao mpaStorageDao;

    public MpaService(MpaStorageDao mpaStorageDao) {
        this.mpaStorageDao = mpaStorageDao;
    }

    public List<Mpa> findAll() {
        return mpaStorageDao.findAll();
    }

    public Mpa findMpaById(long id) {
        return mpaStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Рейтинг с id = %s не найден", id)));
    }
}
