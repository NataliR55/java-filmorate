package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private static final String ALL_FILMS_SQL_QUERY = "SELECT * FROM Films INNER JOIN Mpa ON Films.mpa_id=Mpa.mpa_id ";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(int id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(ALL_FILMS_SQL_QUERY + "WHERE film_id = ?", new FilmMapper(), id);
            film.setGenres(getGenresFilm(id));
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id:{} not exists.", id);
            throw new NotFoundException(String.format("Film with id:%d is not exist", id));
        }
        return film;
    }

    List<Genre> getGenresFilm(int filmId) {
        String sqlQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
    }

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update("INSERT INTO Films(name, description, releaseDate, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        log.info("Create {}", film);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT film_id FROM Films ORDER BY film_id DESC LIMIT 1");
        if (filmRows.next()) {
            int filmId = Integer.parseInt(filmRows.getString("film_id"));
            setFilmsGenres(film, filmId);
        }
        return film;
    }

    private void setFilmsGenres(Film film, int filmId) {
        if (film.getGenres() != null) {
            Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
            genres.addAll(film.getGenres());
            deleteFilmGenre(filmId);
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO Film_Genre(film_id, genre_id) VALUES(?, ?)", filmId, genre.getId());
            }
        }
    }

    private void deleteFilmGenre(int filmId) {
        jdbcTemplate.update("DELETE FROM Film_Genre WHERE film_id=?", filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        filmFound(id);
        jdbcTemplate.update("UPDATE Films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE film_id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), id);
        log.info("Update {}", film);
        setFilmsGenres(film, id);
        return film;
    }

    private List<Genre> getAllFilmsGenres(int filmId) {
        final String sqlQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id WHERE film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
        return filmGenres;
    }

    private Film updateFilmsGenre(Film film, int filmId) {
        if (film.getGenres() != null && film.getGenres().isEmpty()) {
            deleteFilmGenre(filmId);
        } else {
            setFilmsGenres(film, filmId);
        }
        return getFilm(filmId);
    }

    @Override
    public void filmFound(int id) {
        try {
            jdbcTemplate.queryForObject(ALL_FILMS_SQL_QUERY + "WHERE film_id = ?", new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id:{} not exists.", id);
            throw new NotFoundException(String.format("Film with id:%d is not exist", id));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmsWithoutGenres = jdbcTemplate.query(ALL_FILMS_SQL_QUERY, new FilmMapper());
        return addGenresInFilm(filmsWithoutGenres);
    }
    private List<Film> addGenresInFilm(List<Film> films) {
        final String genreQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(genreQuery);

        for (Film film : films) {
            List<Genre> allFilmsGenres = rows.stream()
                    .filter(stringObjectMap -> (int)stringObjectMap.get("FILM_ID") == film.getId())
                    .map(stringObjectMap -> {
                        Genre genre = new Genre();
                        genre.setId((Integer) stringObjectMap.get("GENRE_ID"));
                        genre.setName((String) stringObjectMap.get("NAME"));
                        return genre;
                    })
                    .collect(Collectors.toList());
            film.setGenres(allFilmsGenres);
        }
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        final String sqlQuery = ALL_FILMS_SQL_QUERY +
                "WHERE film_id IN (SELECT film_id FROM Likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";
        //SELECT COALESCE(column_name, 0) FROM table_name; //замена null На 0

        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, new FilmMapper(), count);

        if (popularFilms.size() < count) {
            List<Film> additionalFilms = getAllFilms();
            popularFilms.removeAll(additionalFilms);
            popularFilms.addAll(additionalFilms);
        }
        return null;//addGenresInFilm(popularFilms);
    }

    @Override
    public void like(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO Likes(film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM Likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public void deleteLikes(int userId) {

    }

    @Override
    public void clearAllFilms() {

    }

    @Override
    public void clearAllLikes() {

    }

    @Override
    public void deleteFilm(int id) {

    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM Genres", new GenreMapper());
    }

    @Override
    public Genre getGenreById(int id) {
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject("SELECT * FROM Genres WHERE genre_id=?", new GenreMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Genre with id:{} not exists.", id);
            throw new NotFoundException(String.format("Genre with id:%d is not exist", id));
        }
        return genre;
    }

    @Override
    public List<Mpa> getMpaRatings() {
        return jdbcTemplate.query("SELECT * FROM Mpa", new MpaMapper());
    }

    @Override
    public Mpa getMpaById(int id) {
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT * FROM Mpa WHERE mpa_id=?", new MpaMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Mpa with id:{} not exists.", id);
            throw new NotFoundException(String.format("Mpa with id: %d is not exist", id));
        }
        return mpa;
    }
}
