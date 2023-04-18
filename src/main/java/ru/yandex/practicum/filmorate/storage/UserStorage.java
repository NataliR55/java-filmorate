package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUser(int id);

    void isExistById(int id);

    List<User> getAllUsers();

    void addFriend(int id, int friendId, StatusFriendship statusFriendship);

    void updateStatusFriend(int id, int friendId, StatusFriendship statusFriendship);

    StatusFriendship getStatusFriendship(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getUsersFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    void clearAllUser();

    void deleteUser(int id);
}
