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
    private final Map<Integer, Set<Integer>> userFriends = new HashMap<>();
    private int generateId = 0;

    @Override
    public User createUser(User user) {
        if (findUser(user) != null) {
            throw new ValidationException(String.format("%s with login or email already exist!", user));
        }
        user.setId(++generateId);
        users.put(user.getId(), user);
        log.info("Create {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (getUser(id) != null) {
            users.put(id, user);
            log.info("Update {}", user);
        } else {
            log.info("User with id: {} is NOT found!", id);
            throw new NotFoundException("User with id:" + id + " {} is NOT found!");
        }
        return user;
    }

    @Override
    public User getUser(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found!");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersFriends(int id) {
        Set<Integer> friendsId = userFriends.get(id);
        if (friendsId == null) {
            throw new NotFoundException("User with id " + id + " not have friends!");
        }
        List<User> listFriends = new ArrayList<>();
        for (int friendID : friendsId) {
            User friend = getUser(friendID);
            if (friend != null) listFriends.add(friend);
        }
        return listFriends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> friendsId = userFriends.get(id);
        Set<Integer> otherFriendsId = userFriends.get(otherId);
        if (friendsId == null || otherFriendsId == null) {
            throw new NotFoundException("User with id " + id + " and id " + otherId + " not have common friends!");
        }
        List<User> commonFriends = new ArrayList<>();
        for (int friendID : friendsId) {
            if (otherFriendsId.contains(friendID))
                commonFriends.add(getUser(friendID));
        }
        return commonFriends;
    }

    @Override
    public void addFriend(int id, int friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("To User with id " + id + " is not added friend  with id " + friendId);
        }
        Set<Integer> friendsId = userFriends.get(id);
        if (friendsId == null) {
            friendsId = new HashSet<>();
            friendsId.add(friendId);
            userFriends.put(id, friendsId);
        } else {
            friendsId.add(friendId);
        }
        log.info("User with id " + friendId + ", app to friends user id " + id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        Set<Integer> friendsId = userFriends.get(id);
        if (friendsId != null) {
            if (friendsId.contains(friendId)) {
                friendsId.remove(friendId);
                log.info("User with id " + friendId + "is delete from friends user id " + id);
                if (friendsId.size() == 0) userFriends.remove(id);
            } else {
                throw new NotFoundException("User with id " + friendId + "is not friends user with id " + id);
            }
        } else {
            throw new NotFoundException("User with id " + id + "is not have friends");
        }
    }

    @Override
    public void clearAllUser() {
        userFriends.clear();
        users.clear();
        generateId = 0;
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
}

