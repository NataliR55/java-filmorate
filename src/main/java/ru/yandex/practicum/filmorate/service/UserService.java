package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User createUser(User user) {
        if (user == null) {
            log.info("User is null!");
            throw new ValidationException(String.format("User is null!"));
        }
        return userStorage.createUser(rebuildUser(user));
    }

    public User updateUser(User user) {
        return userStorage.updateUser(rebuildUser(user));
    }

    public User getUser(int id) {
        userStorage.isExistById(id);
        return userStorage.getUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int id1, int id2) {
        StatusFriendship statusFriendship12 = userStorage.getStatusFriendship(id1, id2);
        StatusFriendship statusFriendship21 = userStorage.getStatusFriendship(id2, id1);
        if (statusFriendship21 == StatusFriendship.UNCONFIRMED) {
            statusFriendship21 = StatusFriendship.CONFIRMED;
            userStorage.updateStatusFriend(id2, id1, statusFriendship21);
        }
        if (statusFriendship12 == StatusFriendship.NOSTATUS) {
            userStorage.addFriend(id1, id2, statusFriendship21 == StatusFriendship.CONFIRMED
                    ? StatusFriendship.CONFIRMED : StatusFriendship.UNCONFIRMED);
        }
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getUsersFriends(int id) {
        return userStorage.getUsersFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        try {
            userStorage.isExistById(id);
            userStorage.isExistById(otherId);
            return userStorage.getCommonFriends(id, otherId);
        } catch (NotFoundException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public void clearAllUsers() {
        userStorage.clearAllUser();
        filmStorage.clearAllLikes();
    }

    public void deleteUser(int id) {
        userStorage.isExistById(id);
        filmStorage.deleteLikes(id);
        userStorage.deleteUser(id);
    }

    private User rebuildUser(User user) {
        return User.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .build();
    }
}
