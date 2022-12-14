package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Positive
    @Nullable
    private Long id;

    @Email
    @NotNull
    private String email;

    @Pattern(regexp = "^[0-9a-zA-Z_\\-]+$")
    @NotNull
    private String login;

    @Nullable
    private String name;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }
}
