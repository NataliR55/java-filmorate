package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void testGenres() {
        Genre genre = filmStorage.getGenreById(2);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Драма");
        List<Genre> genres = filmStorage.getGenres();
        assertThat(genres.size()).isEqualTo(6);
        assertThat(genres.get(4).getName()).isEqualTo("Документальный");
    }

    @Test
    void testMpas() {
        Mpa mpa = filmStorage.getMpaById(3);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG-13");
        List<Mpa> mpas = filmStorage.getMpaRatings();
        assertThat(mpas.size()).isEqualTo(5);
        assertThat(mpas.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    public void testFilm() {
        Film film = createFilm(1);
        Film filmFromDb = filmStorage.createFilm(film);
        int filmId = filmFromDb.getId();
        assertThat(filmFromDb).isNotNull();
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("name", film.getName());
        String newNameFilm = "Doom";
        film.setName(newNameFilm);
        LocalDate newDateFilm = LocalDate.of(2005, 1, 1);
        film.setReleaseDate(newDateFilm);
        Mpa mpa = filmStorage.getMpaById(3);
        film.setMpa(mpa);
        List<Genre> newGenres = List.of(filmStorage.getGenreById(1), filmStorage.getGenreById(3), filmStorage.getGenreById(4));
        film.setGenres(newGenres);
        filmStorage.updateFilm(film);
        filmFromDb = filmStorage.getFilm(filmId);
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("name", newNameFilm);
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("releaseDate", newDateFilm);
        assertThat(filmFromDb.getMpa()).hasFieldOrPropertyWithValue("name", "PG-13");
        assertThat(filmFromDb.getGenres().size()).isEqualTo(3);
        assertThat(filmFromDb.getGenres().get(2).getName()).isEqualTo("Триллер");
    }


    @Test
    public void testUser() {
        User userFromDb1 = userStorage.createUser(createUser(1));
        int id1 = userFromDb1.getId();
        User userFromDb2 = userStorage.createUser(createUser(2));
        int id2 = userFromDb2.getId();
        userStorage.addFriend(id1, id2, StatusFriendship.UNCONFIRMED);
        userStorage.addFriend(id2, id1, StatusFriendship.CONFIRMED);
        assertThat(userStorage.getStatusFriendship(id1, id2)).isNotNull();
        assertThat(userStorage.getStatusFriendship(id1, id2).name()).isEqualTo("UNCONFIRMED");
        assertThat(userStorage.getStatusFriendship(id2, id1)).isNotNull();
        assertThat(userStorage.getStatusFriendship(id2, id1).name()).isEqualTo("CONFIRMED");
        User user = createUser(3);
        user.setId(1);
        userStorage.updateUser(user);
        assertThat(userStorage.getUser(1).getName()).isEqualTo(user.getName());
        assertThat(userStorage.getAllUsers().size()).isEqualTo(2);
    }

    private Film createFilm(int id) {
        int mpaID = getRandom(1, 4);
        Mpa mpa = filmStorage.getMpaById(mpaID);
        int amountFilmGenres = getRandom(1, 3);
        List<Genre> genreslist = new ArrayList<>();
        for (int i = 0; i <= amountFilmGenres; i++) {
            int genresID = getRandom(1, 5);
            genreslist.add(filmStorage.getGenreById(genresID));
        }
        return Film.builder()
                .id(0)
                .name("nameFilm" + id)
                .description("descriptionFilm" + id)
                .duration((int) (Math.random() * 160))
                .releaseDate(LocalDate.of(1990, 1, (id < 31 && id > 0 ? id : 1)))
                .mpa(mpa)
                .genres(genreslist)
                .build();
    }

    private User createUser(int id) {
        return User.builder()
                .id(0)
                .login("login" + id)
                .email("user" + id + "@mail.ru")
                .name("NameUser" + id)
                .birthday(LocalDate.of(2000, 1, (id < 31 ? id : 1))).build();
    }

    public int getRandom(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}