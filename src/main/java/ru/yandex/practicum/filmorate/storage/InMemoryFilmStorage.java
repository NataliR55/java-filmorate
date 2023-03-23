package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new HashMap<>();
    private int generateId = 0;

    @Override
    public int createFilm(Film film) {
        return 0;
    }

    @Override
    public Film update(Film film, int id) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }

    @Override
    public Film getFilm(int id) {
        return null;
    }

    @Override
    public void like(int filmId, int userId) {

    }

    @Override
    public int deleteLike(int filmId, long userId) {
        return 0;
    }
}
