package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ReviewReaction {
    Long reviewId;
    Long userId;
    Boolean isLike;
}
