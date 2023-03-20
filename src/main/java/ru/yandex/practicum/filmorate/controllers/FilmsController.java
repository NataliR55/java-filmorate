package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmsController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int generateId = 0;
    public static final LocalDate controlDate = LocalDate.of(1895, 12, 28);

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        if (!checkReleaseDate(film)) throw new ValidationException();
        film.setId(++generateId);
        films.put(film.getId(), film);
        log.info("Create {}", film);
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        if (film == null) return null;
        int id = film.getId();
        if (!films.containsKey(id)) {
            log.info("Film with id: {} is NOT found!", id);
            throw new NotFoundException();
        }
        if (!checkReleaseDate(film)) throw new ValidationException();
        films.put(id, film);
        log.info("Update {}!", film);
        return film;
    }

    public List<Film> getAllFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @GetMapping()
    public List<Film> getAll() {
        List<Film> list = getAllFilms();
        log.info("{}", list);
        return list;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") Integer id) {
        if (id == null) {
            log.info("Film id is null!");
            throw new ValidationException();
        }
        Film film = films.get(id);
        if (film == null) {
            log.info("Film with id: {} is NOT found!", id);
            throw new NotFoundException();
        }
        return film;
    }

    public void clearAll() {
        generateId = 0;
        films.clear();
    }

    public Film getById(int id) {
        return films.get(id);
    }

    private static boolean checkReleaseDate(Film film) {
        if (film.getReleaseDate() == null) {
            log.info("Date must not be null");
            return false;
        }
        if (film.getReleaseDate().isBefore(controlDate)) {
            log.info("{} Release Date not by before {}", film.getReleaseDate(), controlDate);
            return false;
        }
        return true;
    }
}
