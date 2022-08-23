package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        List<Mpa> mpas = mpaService.findAll();
        log.info("Получен список всех рейтингов.");
        return mpas;
    }

    @GetMapping(value = "/{id}")
    public Mpa findMpaById(@PathVariable("id") long id){
        Mpa mpa = mpaService.findMpaById(id);
        log.info("Получен рейтинг {}", id);
        return mpa;
    }
}
