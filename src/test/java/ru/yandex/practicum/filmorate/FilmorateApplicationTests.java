package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    void contextLoads() {
    }

    @Test
    public void testFindUserById() {
        User user1 = User.builder().id(1).name("tttt").email("ee@mail.ru").login("u26354").birthday(LocalDate.of(2000, 1, 1)).build();
        //user1 = null;
        Optional<User> userOptional0 = Optional.ofNullable(user1);
        //Optional<User> userOptional = Optional.ofNullable(user1);
        Optional<User> userOptional = userOptional0;
        //Optional<User> userOptional = userStorage.findUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}