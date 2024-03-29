package ru.yandex.practicum.filmorate.model.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @Email(message = "Field: Email must have the format EMAIL!")
    @NotBlank(message = "Field: Email must be filled!")
    private String email;
    @Pattern(regexp = "^[a-zA-z]{1}[a-zA-Z0-9_]{4,20}$", message = "Field: login is fails format!")
    private String login;
    private String name;
    @Past(message = "Date of birth must be less than today")
    private LocalDate birthday;

    public static class UserBuilder {
        private String login;
        private String name;

        public UserBuilder login(String login) {
            this.login = login.trim();
            return this;
        }

        public UserBuilder name(String name) {
            this.name = (name == null || name.isBlank()) ? login : name.trim();
            return this;
        }
    }
}

