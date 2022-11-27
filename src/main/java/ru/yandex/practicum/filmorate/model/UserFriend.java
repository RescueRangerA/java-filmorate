package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFriend {
    @Positive
    @Nullable
    private Long id;

    @Positive
    private Long userIdA;

    @Positive
    private Long userIdB;
}
