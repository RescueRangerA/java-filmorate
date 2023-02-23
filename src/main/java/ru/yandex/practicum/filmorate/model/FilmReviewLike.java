package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmReviewLike {
    @NonNull
    private FilmReview filmReview;

    @NonNull
    private User user;

    @NonNull
    private Boolean positive;
}
