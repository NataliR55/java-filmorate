package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User getUser(int id);

    List<User> getAllUsers();

    List<User> getUsersFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    User createUser(User user);

    User update(User user, int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
