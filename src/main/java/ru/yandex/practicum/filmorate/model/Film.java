package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

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

    @NotNull
    private FilmMpaRating mpa;

    private Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));

    public Film(@Nullable Long id, String name, String description, LocalDate releaseDate, Integer duration, FilmMpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
