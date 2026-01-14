package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    User user;
    User friend;
    boolean isApproved;
}
