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

    public UserFriend(Long fromUserId, Long toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = Status.PENDING;
    }

    @Positive
    private Long fromUserId;

    @Positive
    private Long toUserId;

    @Positive
    private Status status;
}
