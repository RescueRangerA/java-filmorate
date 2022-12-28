package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    @Positive
    @Nullable
    private Long eventId;

    private Long timestamp;

    @Positive
    @Nullable
    private Long userId;

    @NotBlank
    @NotNull
    private Enum<EventType> eventType;

    @NotBlank
    @NotNull
    private Enum<OperationType> operation;

    @Positive
    @NotNull
    private Long entityId;

    public Feed(
            Long userId,
            Enum<EventType> eventType,
            Enum<OperationType> operation,
            Long entityId
    ) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
