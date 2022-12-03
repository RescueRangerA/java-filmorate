package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFriend {
    public enum Status {
        PENDING, CONFIRMED
    }

    public UserFriend(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = Status.PENDING;
    }

    @Positive
    private User fromUser;

    @Positive
    private User toUser;

    @Positive
    private Status status;
}
