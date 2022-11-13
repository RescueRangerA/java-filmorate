package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validation.constraints.AfterTheDate;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class Film implements Entity {
    @Positive
    @Nullable
    private Long id;

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
}
