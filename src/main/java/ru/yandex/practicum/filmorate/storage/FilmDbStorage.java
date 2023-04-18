package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
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

    @Override
    public void filmFound(int id) {
        try {
            jdbcTemplate.queryForObject(ALL_FILMS_SQL_QUERY + "WHERE film_id = ?", new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id:{} not exists.", id);
            throw new NotFoundException(String.format("Film with id:%d is not exist", id));
        }
    }

    List<Genre> getGenresFilm(int filmId) {
        String sqlQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO Films(name, description, releaseDate, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        int mpaId = film.getMpa().getId();
        getMpaById(mpaId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, mpaId);
            return stmt;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        setFilmsGenres(film);
        log.info("Create {}", film);
        return film;
    }

    private void setFilmsGenres(Film film) {
        int filmId = film.getId();
        if (film.getGenres() != null) {
            Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
            genres.addAll(film.getGenres());
            deleteFilmGenre(filmId);
            for (Genre genre : genres) {
                getGenreById(genre.getId());
                jdbcTemplate.update("INSERT INTO Film_Genre(film_id, genre_id) VALUES(?, ?)", filmId, genre.getId());
            }
        }
    }

    private void deleteFilmGenre(int filmId) {
        jdbcTemplate.update("DELETE FROM Film_Genre WHERE film_id=?", filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        filmFound(filmId);
        int mpaId = film.getMpa().getId();
        getMpaById(mpaId);
        String sqlQuery = "UPDATE Films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaId, filmId);
        log.info("Update {}", film);
        return updateFilmsGenre(film);
    }

    private List<Genre> getAllFilmsGenres(int filmId) {
        final String sqlQuery = "SELECT * FROM Film_Genre JOIN Genres ON Film_Genre.genre_id=Genres.genre_id WHERE film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sqlQuery, new GenreMapper(), filmId);
        return filmGenres;
    }

    private Film updateFilmsGenre(Film film) {
        int filmId = film.getId();
        if (film.getGenres() != null && film.getGenres().isEmpty()) {
            deleteFilmGenre(filmId);
        } else {
            setFilmsGenres(film);
        }
        return getFilm(filmId);
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
                    .filter(stringObjectMap -> (int) stringObjectMap.get("FILM_ID") == film.getId())
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
        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, new FilmMapper(), count);
        if (popularFilms.size() < count) {
            List<Film> additionalFilms = getAllFilms();
            popularFilms.removeAll(additionalFilms);
            popularFilms.addAll(additionalFilms);
        }
        return addGenresInFilm(popularFilms);
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
        jdbcTemplate.update("DELETE FROM likes WHERE user_id=?", userId);
    }

    @Override
    public void clearAllFilms() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
    }

    @Override
    public void clearAllLikes() {
        jdbcTemplate.update("DELETE FROM likes");
    }

    @Override
    public void deleteFilm(int id) {
        filmFound(id);
        String sqlQuery = "DELETE FROM likes WHERE FILM_ID=?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "DELETE FROM film_genre WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "DELETE FROM films WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
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
