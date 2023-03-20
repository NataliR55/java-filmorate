package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }
/*
    Film getFilm(int id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int countTopFilms);

    Film create(Film film);

    public Film update(Film film, int id);

    void like(int filmId, int userId);

    void deleteLike(int filmId, long userId);
*/
}
