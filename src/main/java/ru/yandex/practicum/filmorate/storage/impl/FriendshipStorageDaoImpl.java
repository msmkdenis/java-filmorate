package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorageDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class FriendshipStorageDaoImpl implements FriendshipStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Friendship friendship) {
        final String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, friendship.getUser().getId(), friendship.getFriend().getId());
    }

    @Override
    public void removeFriend(Friendship friendship) {
        final String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, friendship.getUser().getId(), friendship.getFriend().getId());
    }

    @Override
    public List<User> findUserFriends(long userId) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM USERS, FRIENDS " +
                        "WHERE USERS.USER_ID = FRIENDS.FRIEND_ID AND FRIENDS.USER_ID = ?", userId);

        return findUsers(sqlQuery);
    }

    @Override
    public List<User> findMutualFriends(long userId, long otherId) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM USERS U, FRIENDS F, FRIENDS O " +
                        "WHERE U.USER_ID = F.FRIEND_ID AND U.USER_ID = O.FRIEND_ID AND F.USER_ID = ? AND O.USER_ID = ?",
                userId, otherId);

        return findUsers(sqlQuery);
    }

    private List<User> findUsers (SqlRowSet sqlQuery) {
        List<User> users = new ArrayList<>();
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (sqlQuery.next()) {
            User user = new User(sqlQuery.getLong("USER_ID"),
                    sqlQuery.getString("EMAIL"),
                    sqlQuery.getString("LOGIN"),
                    sqlQuery.getString("USER_NAME"),
                    LocalDate.parse(Objects.requireNonNull(sqlQuery.getString("BIRTHDAY")), dtf));
            users.add(user);
        }
        return users;
    }
}
