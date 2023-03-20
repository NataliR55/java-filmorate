package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.FilmsController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmsController filmsController;

    @AfterEach
    public void clearAll() {
        filmsController.clearAll();
    }

    @Test
    public void getFilms() throws Exception {
        Film[] films = {createOne(1), createOne(2), createOne(3), createOne(4)};
        assertEquals(filmsController.getAllFilms().size(), 0);
        for (Film film : films) {
            mockMvc.perform(post("/films")
                    .content(objectMapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON)
            );
        }
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
        assertEquals(filmsController.getAllFilms().size(), 4);
        assertEquals(filmsController.getById(3).getName(), "nameFilm3");
    }

    @Test
    public void checkName() throws Exception {
        Film film = createOne(1);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        String[] badNames = {"", "     "};
        for (int i = 0; i < badNames.length; i++) {
            film = createOne(i + 2);
            film.setName(badNames[i]);
            mockMvc.perform(post("/films")
                            .content(objectMapper.writeValueAsString(film))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }
        assertEquals(filmsController.getAllFilms().size(), 1);
        assertEquals(filmsController.getById(1).getName(), "nameFilm1");
    }

    @Test
    public void checkDescription() throws Exception {
        Film film = createOne(1);
        film.setDescription("a".repeat(300));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getAllFilms().size(), 0);
        film.setDescription("b".repeat(200));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        assertEquals(filmsController.getAllFilms().size(), 1);
        film = createOne(2);
        film.setDescription("b".repeat(201));
        film.setId(1);
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getById(1).getDescription(), "b".repeat(200));


    }

    @Test
    public void checkReleaseDate() throws Exception {
        Film film = createOne(1);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getAllFilms().size(), 0);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        System.out.println(filmsController.getAllFilms());
        assertEquals(filmsController.getAllFilms().size(), 1);
        assertEquals(filmsController.getById(1).getId(), 1);
        film = createOne(1);
        film.setReleaseDate(LocalDate.of(1800, 12, 28));
        film.setId(1);
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getById(1).getReleaseDate(), LocalDate.of(1895, 12, 28));
    }

    @Test
    public void checkDuration() throws Exception {
        Film film = createOne(1);
        film.setDuration(-10);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getAllFilms().size(), 0);
        film.setDuration(100);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        System.out.println(filmsController.getAllFilms());
        assertEquals(filmsController.getAllFilms().size(), 1);
        assertEquals(filmsController.getById(1).getDuration(), 100);
        film = createOne(2);
        film.setDuration(0);
        film.setId(1);
        mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(filmsController.getById(1).getDuration(), 100);
    }

    private Film createOne(int id) {
        return Film.builder()
                .id(0)
                .name("nameFilm" + id)
                .description("descriptionFilm" + id)
                .duration((int) (Math.random() * 160))
                .releaseDate(LocalDate.of(1990, 1, (id < 31 ? id : 1))).build();
    }
}
