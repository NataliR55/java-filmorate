package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        if (film == null) throw new ValidationException("Film not be null");
        film.setId(++generateId);
        films.put(film.getId(), film);
        log.info("Create {}", film);
        return film;

    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (getFilm(id) != null) {
            films.put(id, film);
            log.info("Update {}", film);
        } else {
            throw new NotFoundException(String.format("%s is not found and not update%n", film));
        }
        return film;
    }

    @Override
    public Film getFilm(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("Film with id %d not found!%n", id));
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        count = count == 0 ? 10 : count;

        Comparator<Integer> comparator = new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -1 * (o1 - o2);
            }
        };
        Map<Integer, Film> popularFilms = new TreeMap<>(comparator);
        for (Map.Entry<Integer, Set<Integer>> entry : likes.entrySet()) {
            popularFilms.put(entry.getValue().size(), getFilm(entry.getKey()));
        }
        return popularFilms.values().stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void like(int filmId, int userId) {
        Film film = getFilm(filmId);
        Set<Integer> likesFilm = likes.get(filmId);
        if (likesFilm == null) {
            likesFilm = new HashSet<>();
            likesFilm.add(userId);
            likes.put(filmId, likesFilm);
        } else {
            likesFilm.add(userId);
        }
        log.info("Add like to {} ", film);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        Set<Integer> likesFilm = likes.get(filmId);
        if (likesFilm != null) {
            if (likesFilm.contains(userId)) {
                likesFilm.remove(userId);
                log.info("Like user id {} is delete from ", userId, film);
                if (likesFilm.size() == 0) likes.remove(filmId);
            } else {
                throw new NotFoundException(String.format("%s note have like user with id %s%n", film, userId));
            }
        } else {
            throw new NotFoundException(String.format("%s note have like%n", film));
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
            throw new NotFoundException(String.format("Film with id %d not found", id));
        }
    }
}
