package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Size(max = 200, message = "The description must be no more than 200 characters long!")
    @EqualsAndHashCode.Exclude
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Duration film must be positive!")
    private int duration;

    public static boolean checkReleaseDate(Film film) {
        if (film.releaseDate == null) {
            log.info("{} Release Date not by null ", film.releaseDate);
            return false;
        }
        if (film.releaseDate.isBefore(controlDate)) {
            log.info("{} Release Date not by before {}", film.releaseDate, controlDate);
            return false;
        }
        return true;
    }

}
