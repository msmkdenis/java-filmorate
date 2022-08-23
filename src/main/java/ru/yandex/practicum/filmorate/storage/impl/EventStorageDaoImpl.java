package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.EventStorageDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
public class EventStorageDaoImpl implements EventStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public EventStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriendEvent(Long id, Long friendId) {
        Event event = getBaseEvent(id, friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(EventOperation.ADD);
        add(event);
    }

    @Override
    public void deleteFriendEvent(Long id, Long friendId){
        Event event = getBaseEvent(id, friendId);
        event.setEventType(EventType.FRIEND);
        event.setOperation(EventOperation.REMOVE);
        add(event);
    }

    @Override
    public void addLikeEvent(Long filmId, Long userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(EventOperation.ADD);
        add(event);
    }

    @Override
    public void deleteLikeEvent(Long filmId, Long userId) {
        Event event = getBaseEvent(userId, filmId);
        event.setEventType(EventType.LIKE);
        event.setOperation(EventOperation.REMOVE);
        add(event);
    }

    @Override
    public void addReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.ADD);
        add(event);
    }

    @Override
    public void updateReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.UPDATE);
        add(event);
    }

    @Override
    public void deleteReviewEvent(Review review) {
        Event event = getBaseEvent(review.getUserId(), review.getReviewId());
        event.setEventType(EventType.REVIEW);
        event.setOperation(EventOperation.REMOVE);
        add(event);
    }

    @Override
    public List<Event> getFeed(Long userId) {
        String sqlQuery = "SELECT EVENT_ID, TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID " +
                          "FROM EVENTS " +
                          "WHERE USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, this::makeLocalEvent, userId);
    }

    private void add(Event event) {
        String sql = "INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement(sql, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType().toString());
            ps.setString(4, event.getOperation().toString());
            ps.setLong(5, event.getEntityId());
            return ps;
        }, keyHolder);

        event.setEventId(keyHolder.getKey().longValue());
    }

    private Event getBaseEvent(Long userId, Long entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setUserId(userId);
        event.setEntityId(entityId);
        return event;
    }

    private Event makeLocalEvent(ResultSet rs, int num) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getLong("EVENT_ID"));
        event.setTimestamp(rs.getLong("TIMESTAMP"));
        event.setUserId(rs.getLong("USER_ID"));
        event.setEventType(EventType.valueOf(rs.getString("EVENT_TYPE")));
        event.setOperation(EventOperation.valueOf(rs.getString("OPERATION")));
        event.setEntityId(rs.getLong("ENTITY_ID"));
        return event;
    }
}
