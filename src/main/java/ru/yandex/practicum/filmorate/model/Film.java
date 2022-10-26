package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.constraints.AfterTheDate;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class Film {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @AfterTheDate(message = "Release date should not be less 1895-12-28", moment = "1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
