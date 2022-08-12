package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorageDao;

import java.util.ArrayList;
import java.util.List;

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
    public List<Long> findUserFriends(long userId) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT USER_ID " +
                    "FROM USERS " +
                    "WHERE USER_ID IN " +
                      "(SELECT FRIEND_ID " +
                      "FROM FRIENDS " +
                      "WHERE USER_ID = ?)", userId);

        final List<Long> friendsId = new ArrayList<>();
        while (sqlQuery.next()) {
            Long user = sqlQuery.getLong("USER_ID");
            friendsId.add(user);
        }
        return friendsId;
    }

    @Override
    public List<Long> findMutualFriends(long userId, long otherId) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT USER_ID " +
                    "FROM USERS " +
                    "WHERE USER_ID IN " +
                      "(SELECT U.FRIEND_ID " +
                      "FROM FRIENDS U, FRIENDS O " +
                      "WHERE U.FRIEND_ID = O.FRIEND_ID " +
                      "AND U.USER_ID = ? " +
                      "AND O.USER_ID = ?)", userId, otherId);

        final List<Long> friendsId = new ArrayList<>();
        while (sqlQuery.next()) {
            Long user = sqlQuery.getLong("USER_ID");
            friendsId.add(user);
        }
        return friendsId;
    }
}
