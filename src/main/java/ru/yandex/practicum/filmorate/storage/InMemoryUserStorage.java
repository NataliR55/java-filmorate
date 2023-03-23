package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int generateId = 0;

    @Override
    public User getUser(int id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User with id " + id + " not found!");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersFriends(int id) {
        User user = getUser(id);
        List<User> listUsers = new ArrayList<>();
        for (long idUser : user.getFriends()) {
            User friend = getUser((int) idUser);
            if (friend != null) listUsers.add(getUser((int) idUser));
        }
        return listUsers;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        getUsersFriends(id);
        return null;
    }

    @Override
    public User createUser(User user) {
        if (findUser(user) != null) {
            throw new ValidationException(String.format("%s with login or email already exist!", user));
        }
        user.setId(++generateId);
        changeLoginAndNameUser(user);
        users.put(user.getId(), user);
        log.info("Create {}", user);
        return user;
    }

    @Override
    public User update(User user, int id) {
        return null;
    }

    @Override
    public void addFriend(int id, int friendId) {

    }

    @Override
    public void deleteFriend(int id, int friendId) {

    }

    private User findUser(User user) {
        if (user == null) return null;
        Optional<User> userFound;
        String email = user.getEmail();
        if ((email != null) && (!email.isBlank())) {
            userFound = users.values().stream().filter(t -> t.getEmail().toLowerCase()
                    .equals(email.toLowerCase().trim())).findFirst();
            if (userFound.isPresent()) return userFound.get();
        }
        String login = user.getLogin();
        if ((login != null) && (!login.isBlank())) {
            userFound = users.values().stream().filter(t -> t.getLogin().equals(login.trim())).findFirst();
            if (userFound.isPresent()) return userFound.get();
        }
        return null;
    }

    private void changeLoginAndNameUser(User user) {
        if (user == null) return;
        String login = user.getLogin() == null ? null : user.getLogin();
        String name = user.getName() == null ? null : user.getName().trim();
        String userName = (name == null || name.isBlank()) ? login : name;
        user.setName(userName);
        user.setLogin(login);
    }

}
