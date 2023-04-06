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
        findUserByDetails(user);
        user.setId(++generateId);
        users.put(user.getId(), user);
        log.info("Create {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        isExistById(id);
        users.put(id, user);
        log.info("Update {}", user);
        return user;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersFriends(int id) {
        Set<Integer> friendsId = userFriends.get(id);
        List<User> listFriends = new ArrayList<>();
        if (friendsId == null) {
            log.info(String.format("User with id: %d not have friends!", id));
            return listFriends;
        }
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
        List<User> commonFriends = new ArrayList<>();
        if (friendsId == null || otherFriendsId == null) {
            log.info(String.format("User with id: %d  and id:%d not have common friends!", id, otherId));
            return commonFriends;
        }
        for (int friendID : friendsId) {
            if (otherFriendsId.contains(friendID))
                commonFriends.add(getUser(friendID));
        }
        return commonFriends;
    }

    @Override
    public void isExistById(int id) {
        if (users.get(id) == null) {
            log.info("User with id:{} not exists.", id);
            throw new NotFoundException(String.format("User with id: %d  is not exist", id));
        }
    }

    @Override
    public void addFriend(int id, int friendId) {
        isExistById(id);
        isExistById(friendId);
        for (int i = 0; i < 2; i++) {
            int idUser = i == 0 ? id : friendId;
            int idFriend = i == 0 ? friendId : id;
            Set<Integer> idFriends = userFriends.get(idUser);
            if (idFriends == null) {
                idFriends = new HashSet<>();
                idFriends.add(idFriend);
                userFriends.put(idUser, idFriends);
            } else {
                idFriends.add(friendId);
            }
        }
        log.info("User with id:{} add to friends user id:{} ", friendId, id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        isExistById(id);
        isExistById(friendId);
        for (int i = 0; i < 2; i++) {
            int idUser = i == 0 ? id : friendId;
            int idFriend = i == 0 ? friendId : id;
            Set<Integer> idFriends = userFriends.get(idUser);
            if (idFriends != null) {
                if (idFriends.contains(idFriend)) {
                    idFriends.remove(idFriend);
                    log.info("User with id:{} is delete from friends user id:{} ", idFriend, idUser);
                    if (idFriends.size() == 0) userFriends.remove(idUser);
                } else {
                    throw new NotFoundException(String.format("User with id: %d is not friends user with id: %d", idFriend, idUser));
                }
            } else {
                throw new NotFoundException(String.format("User with id: %d is not have friends", idUser));
            }
        }
    }

    @Override
    public void clearAllUser() {
        userFriends.clear();
        users.clear();
        generateId = 0;
    }

    @Override
    public void deleteUser(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User with id {} delete", id);
            if (userFriends.containsKey(id)) {
                for (int i : userFriends.get(id)) {
                    userFriends.get(i).remove(id);
                }
                userFriends.remove(id);
            }
        } else {
            throw new NotFoundException(String.format("Film with id: %d not found", id));
        }
    }

    private void findUserByDetails(User user) {
        if (user == null) {
            log.info("User is null!");
            throw new ValidationException(String.format("User is null!"));
        }
        Optional<User> userFound;
        String email = user.getEmail();
        if ((email != null) && (!email.isBlank())) {
            userFound = users.values().stream().filter(t -> t.getEmail().toLowerCase()
                    .equals(email.toLowerCase().trim())).findFirst();
            if (userFound.isPresent()) {
                log.info("User with login {} already exist!", user.getLogin());
                throw new ValidationException(String.format("User with login %s already exist!", user.getLogin()));
            }
        }
        String login = user.getLogin();
        if ((login != null) && (!login.isBlank())) {
            userFound = users.values().stream().filter(t -> t.getLogin().equals(login.trim())).findFirst();
            if (userFound.isPresent()) {
                log.info("User with email {} already exist!", user.getEmail());
                throw new ValidationException(String.format("User with email %s already exist!", user.getEmail()));
            }
        }
    }
}

