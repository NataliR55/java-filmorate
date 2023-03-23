package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    User create(User user){
        return user;
    }

    User getUser(int id) {
        return user;
    }

    List<User> getAllUsers(){
        return List.of(new User());
    }

    List<User> getUsersFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    User update(User user);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);


}
