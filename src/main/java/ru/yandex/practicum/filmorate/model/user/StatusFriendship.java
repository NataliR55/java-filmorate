package ru.yandex.practicum.filmorate.model.user;

public enum StatusFriendship {
    NOSTATUS("NOSTATUS"),
    UNCONFIRMED("UNCONFIRMED"),
    CONFIRMED("CONFIRMED");

    private String status;

    public String getStatus() {
        return status;
    }

    StatusFriendship(String status) {
        this.status = status;
    }
}
