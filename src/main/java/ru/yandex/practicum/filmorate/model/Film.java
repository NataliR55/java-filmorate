package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Slf4j
@Data
@Builder
public class Film {
    public static final LocalDate controlDate = LocalDate.of(1895, 12, 28);
    private int id;
    private String name;
    @EqualsAndHashCode.Exclude
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private int duration;

    public static class FilmBuilder {
        private String name;
        private String description;
        private LocalDate releaseDate;
        private int duration;

        public FilmBuilder name(String name) {
            if (name == null || name.isBlank())
                throw new ValidationException("Field: name must be filled!");
            this.name = name;
            return this;
        }

        public FilmBuilder description(String description) {
            if (description != null && description.length() > 200)
                throw new ValidationException("The description must be no more than 200 characters long!");
            this.description = description;
            return this;
        }

        public FilmBuilder releaseDate(LocalDate releaseDate) {
            if (releaseDate == null) throw new ValidationException("Release Date not by null ");
            if (releaseDate.isBefore(controlDate)) {
                throw new ValidationException("Release Date not by before " + controlDate);
            } else {
                this.releaseDate = releaseDate;
            }
            return this;
        }

        public FilmBuilder duration(int duration) {
            if (duration <= 0) throw new ValidationException("Duration film must be positive");
            this.duration = duration;
            return this;
        }

    }
}
