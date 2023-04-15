package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int id);

    void filmFound(int id);

    List<Film> getAllFilms();


    List<Film> getPopularFilms(int count);

    void like(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    void deleteLikes(int userId);

    void clearAllFilms();

    void clearAllLikes();

    void deleteFilm(int id);

}
