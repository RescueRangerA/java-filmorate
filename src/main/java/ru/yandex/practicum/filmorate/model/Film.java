package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.*;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.constraints.AfterTheDate;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Positive
    @Nullable
    private Long filmId;

    @NotBlank
    @NotNull
    private String name;

    @Size(max = 200)
    @NotNull
    private String description;

    @AfterTheDate(message = "Release date should not be less 1895-12-28", moment = "1895-12-28")
    @NotNull
    private LocalDate releaseDate;

    @Positive
    @NotNull
    private Integer duration;

    @Positive
    private Long ratingId;
}
