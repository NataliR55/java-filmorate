package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    private int generateId = 0;

    @Override
    public Film createFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film not be null");
        }
        film.setId(++generateId);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());

        log.info("Create {}", film);
        return film;

    }

    @Override
    public Film getFilm(int id) {
        Film film = films.get(id);
        filmFound(id);
        return film;
    }

    @Override
    public void filmFound(int id) {
        if (films.get(id) == null) {
            log.info("Film with id:{} not found.", id);
            throw new NotFoundException(String.format("Film with id: %d  is not found", id));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        filmFound(id);
        films.put(id, film);
        log.info("Update {}", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        count = count == 0 ? 10 : count;
        Map<Integer, Integer> map = likes.entrySet().stream()
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().size()));

        List<Film> listFilms = new ArrayList<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .forEach(entry -> {
                    listFilms.add(films.get(entry.getKey()));
                });
        return listFilms;
    }

    @Override
    public void like(int filmId, int userId) {
        filmFound(filmId);
        likes.get(filmId).add(userId);
        log.info("Add like to film id:{} ", filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        filmFound(filmId);
        Set<Integer> likesFilm = likes.get(filmId);
        if (likesFilm.contains(userId)) {
            likesFilm.remove(userId);
            log.info("Like user id {} is delete from film id:{}", userId, filmId);
        } else {
            throw new NotFoundException(String.format("Film id:%s not have like user with id:%s", filmId, userId));
        }
    }

    @Override
    public void deleteLikes(int userId) {
        for (Set<Integer> likesFilm : likes.values()) {
            if (likesFilm.contains(userId)) {
                likesFilm.remove(userId);
            }
        }
    }

    @Override
    public void clearAllFilms() {
        likes.clear();
        films.clear();
        generateId = 0;
    }

    @Override
    public void clearAllLikes() {
        likes.clear();
    }

    @Override
    public void deleteFilm(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Film with id {} delete", id);
            if (likes.containsKey(id)) likes.remove(id);
        } else {
            throw new NotFoundException(String.format("Film with id: %d not found", id));
        }
    }

    @Override
    public List<Genre> getGenres() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public List<Mpa> getMpaRatings() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Mpa getMpaById(int id) {
        return null;
    }
}
