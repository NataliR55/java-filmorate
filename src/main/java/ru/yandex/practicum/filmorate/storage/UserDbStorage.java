package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(int id) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM Users WHERE user_id=?", new UserMapper(), id);
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM Users", new UserMapper());
    }

    @Override
    public List<User> getUsersFriends(int id) {
        final String sqlQuery = "SELECT * FROM USERS WHERE user_id IN (SELECT friend_id FROM Friends WHERE user_id=?)";
        return jdbcTemplate.query(sqlQuery, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        final String sqlQuery = "SELECT * FROM USERS WHERE user_id IN (SELECT friend_id FROM Friends WHERE user_id=?) " +
                "INTERSECT SELECT * FROM USERS WHERE user_id IN (SELECT friend_id FROM Friends WHERE user_id=?)";
        return jdbcTemplate.query(sqlQuery, new UserMapper(), id, otherId);
    }

    @Override
    public User createUser(User user) {
        jdbcTemplate.update("INSERT INTO Users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        log.info("User created: " + user.getName());
        return jdbcTemplate.queryForObject("SELECT * FROM Users ORDER BY user_id DESC LIMIT 1", new UserMapper());
    }

    public void isExistById(int id) {
        try {
            getUser(id);
        } catch (EmptyResultDataAccessException e) {
            log.info("User with id:{} not exists.", id);
            throw new NotFoundException(String.format("User with id: %d  is not exist", id));
        }
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        isExistById(id);
        final String sqlQuery = "UPDATE Users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
        log.info("Update {}", user);
        return getUser(id);
    }

    @Override
    public void addFriend(int id, int friendId, StatusFriendship status) throws EmptyResultDataAccessException {
        isExistById(id);
        isExistById(friendId);
        jdbcTemplate.update("INSERT INTO Friends (user_id, friend_id,status) VALUES (?, ?, ?)", id, friendId, status.name());
    }

    @Override
    public void updateStatusFriend(int id, int friendId, StatusFriendship status) {
        isExistById(id);
        isExistById(friendId);
        jdbcTemplate.update("UPDATE Friends SET status=? WHERE user_id=? AND friend_id=?", status.name(), id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        jdbcTemplate.update("DELETE FROM Friends WHERE user_id=? AND friend_id=?", id, friendId);
        jdbcTemplate.update("DELETE FROM Friends WHERE user_id=? AND friend_id=?", friendId, id);
    }

    @Override
    public StatusFriendship getStatusFriendship(int id, int friendId) {
        StatusFriendship status;
        final String sqlQuery = "SELECT status from FRIENDS where user_id=? AND friend_id=?";
        try {
            String statusStr = jdbcTemplate.queryForObject(sqlQuery, String.class, id, friendId);
            status = StatusFriendship.valueOf(statusStr);
        } catch (IllegalArgumentException | NullPointerException | DataAccessException e) {
            status = StatusFriendship.NOSTATUS;
        }
        return status;
    }

    @Override
    public void clearAllUser() {
        jdbcTemplate.update("DELETE FROM Users");
    }

    @Override
    public void deleteUser(int id) {
        isExistById(id);
        jdbcTemplate.update("DELETE FROM Users WHERE user_id=?", id);
    }
}