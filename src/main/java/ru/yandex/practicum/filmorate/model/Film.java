package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Slf4j
@Data
@Builder
public class Film {
    public static final LocalDate controlDate = LocalDate.of(1895, 12, 28);
    private int id;
    @NotBlank(message = "Field: name must be filled!")
    private String name;
    @Size(max = 200, message = "Description must be Max 200 character!")
    @EqualsAndHashCode.Exclude
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Duration film must be positive!")
    private int duration;

    public static class FilmBuilder {
        private LocalDate releaseDate;

        public FilmBuilder releaseDate(LocalDate releaseDate) {
            if (releaseDate == null) throw new ValidationException("Release Date not by null ");
            if (releaseDate.isBefore(controlDate)) {
                throw new ValidationException("Release Date not by before " + controlDate);
            } else {
                this.releaseDate = releaseDate;
            }
            return this;
        }

    }
}
