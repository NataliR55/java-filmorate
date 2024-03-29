package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(rebuildFilm(film));
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(rebuildFilm(film));
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
        filmStorage.filmFound(filmId);
        userStorage.isExistById(userId);
        filmStorage.like(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.filmFound(filmId);
        userStorage.isExistById(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public void deleteLikes(int userId) {
        userStorage.isExistById(userId);
        filmStorage.deleteLikes(userId);
    }

    public void clearAllFilms() {
        filmStorage.clearAllFilms();
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    private Film rebuildFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .build();
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getMpaRatings() {
        return filmStorage.getMpaRatings();
    }

    public Mpa getMpaById(int id) {
        return filmStorage.getMpaById(id);
    }
}
