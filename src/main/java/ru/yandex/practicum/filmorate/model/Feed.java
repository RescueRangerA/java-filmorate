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
    private Long id;

    private Long timeStamp;

    @Positive
    @Nullable
    private Long userId;

    @NotBlank
    @NotNull
    private Enum<EventType> eventType;

    @NotBlank
    @NotNull
    private Enum<OperationType> operationType;

    @Positive
    @NotNull
    private Long entityId;

    public Feed(
            @Nullable Long id,
            Long userId,
            Enum<EventType> eventType,
            Enum<OperationType> operationType,
            Long entityId
    ) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.operationType = operationType;
        this.entityId = entityId;
        this.timeStamp = System.currentTimeMillis()/1000;
    }
}
