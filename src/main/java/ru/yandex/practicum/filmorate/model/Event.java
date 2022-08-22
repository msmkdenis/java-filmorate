package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Event {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private EventOperation operation;
    private Long entityId;
}