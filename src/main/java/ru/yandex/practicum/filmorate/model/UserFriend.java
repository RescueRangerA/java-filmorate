package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFriend {
    @Positive
    private User fromUser;

    @Positive
    private User toUser;
}
