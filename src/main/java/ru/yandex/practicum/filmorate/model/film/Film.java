package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
public class Film {
    public static final LocalDate controlDate = LocalDate.of(1895, 12, 28);
    private int id;
    @NotBlank(message = "Name film must be filled!")
    private String name;
    @Size(max = 200, message = "Description must be Max 200 character!")
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull(message = "Release date film must be filled!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Duration film must be positive!")
    private int duration;
    private Mpa mpa;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    public static class FilmBuilder {
        private LocalDate releaseDate;

        public FilmBuilder releaseDate(LocalDate releaseDate) {
            if (releaseDate.isBefore(controlDate)) {
                throw new ValidationException("Release Date not by before " + controlDate);
            } else {
                this.releaseDate = releaseDate;
            }
            return this;
        }
    }
}
