package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserService userService;
    private int generateId = 0;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        if (findUser(user) != null) {
            throw new ValidationException(String.format("%s with login or email already exist!", user));
        }
        user.setId(++generateId);
        changeLoginAndNameUser(user);
        users.put(user.getId(), user);
        log.info("Create {}", user);
        return user;
    }

    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @GetMapping()
    public List<User> getAll() {
        List<User> list = getAllUsers();
        log.info("{}", list);
        return list;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        if (id == null) {
            log.info("User id is null!");
            throw new ValidationException();
        }
        User user = users.get(id);
        if (user == null) {
            log.info("User with id: {} is NOT found!", id);
            throw new NotFoundException();
        }
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        if (user == null) return null;
        int id = user.getId();
        if (!users.containsKey(id)) {
            log.info("User with id: {} is NOT found!", id);
            throw new NotFoundException();
        }
        changeLoginAndNameUser(user);
        users.put(id, user);
        log.info("Update {}!", user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        User user = users.get(id);
        if (user == null) {
            log.info("User with id: {} is not found and not delete!", id);
            throw new NotFoundException();
        }
        users.remove(id);
        log.info("User {} is delete!", user);
    }

    private User findUser(User user) {
        if (user == null) return null;
        Optional<User> userFind;
        String email = user.getEmail();
        if ((email != null) && (!email.isBlank())) {
            userFind = users.values().stream().filter(t -> t.getEmail().toLowerCase()
                    .equals(email.toLowerCase().trim())).findFirst();
            if (userFind.isPresent()) return userFind.get();
        }
        String login = user.getLogin();
        if ((login != null) && (!login.isBlank())) {
            userFind = users.values().stream().filter(t -> t.getLogin().equals(login.trim())).findFirst();
            if (userFind.isPresent()) return userFind.get();
        }
        return null;
    }

    public void clearAll() {
        generateId = 0;
        users.clear();
    }

    public User getById(int id) {
        return users.get(id);
    }

    private void changeLoginAndNameUser(User user) {
        if (user == null) return;
        String login = user.getLogin() == null ? null : user.getLogin();
        String name = user.getName() == null ? null : user.getName().trim();
        String userName = (name == null || name.isBlank()) ? login : name;
        user.setName(userName);
        user.setLogin(login);
    }



    @GetMapping("/{id}")
    public User getUser(@PathVariable(required = false) int id) {
        return userService.getUser(id);
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable int id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(required = false) int id, @PathVariable(required = false) int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userService.create(buildUser(user));
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        return userService.update(buildUser(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }


}
