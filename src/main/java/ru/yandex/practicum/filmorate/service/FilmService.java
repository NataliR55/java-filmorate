package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(rebuildFilm(film));
    }

    public Film updateFilm(Film film) {
        return filmStorage.createFilm(rebuildFilm(film));
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public void like(int filmId, int userId) {
        if (userStorage.getUser(userId) != null)
            filmStorage.like(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (userStorage.getUser(userId) != null)
            filmStorage.deleteLike(filmId, userId);
    }
    public void clearAllFilms() {
        filmStorage.clearAllFilms();
    }

    public void deleteFilm(int id){
        filmStorage.deleteFilm(id);

    }

    private Film rebuildFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }
}
