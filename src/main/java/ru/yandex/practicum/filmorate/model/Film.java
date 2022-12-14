package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

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

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public Film(@Nullable Long id, String name, String description, LocalDate releaseDate, Integer duration, FilmMpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = new LinkedHashSet<>();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void setGenres(LinkedHashSet<Genre> genres) {
        Set<Long> usedIDs = new HashSet<>();
        this.genres = new LinkedHashSet<>();

        for (Genre genre : genres) {
            if (!usedIDs.contains(genre.getId())) {
                this.genres.add(genre);
                usedIDs.add(genre.getId());
            }
        }
    }
}
