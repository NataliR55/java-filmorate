package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User createUser(User user) {
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

    public void addFriend(int id, int friendId) {
        userStorage.addFriend(id, friendId);
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
