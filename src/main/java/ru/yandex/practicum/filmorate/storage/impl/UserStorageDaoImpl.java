package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class UserStorageDaoImpl implements UserStorageDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> add(User user) {
        String sqlQuery = "INSERT INTO USERS(EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                          "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String[] userName = new String[1];
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            final String name = user.getName();
            if (name == null || name.isBlank()) {
                stmt.setString(3, user.getLogin());
                userName[0] = user.getLogin();
            } else {
                stmt.setString(3, user.getName());
                userName[0] = user.getName();
            }
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        user.setName(userName[0]);
        log.info("Добавлен пользователь {}", user.getName());
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        String sqlQuery = "UPDATE USERS " +
                          "SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Обновлен пользователь {}", user.getId());
        return findById(user.getId());
    }

    @Override
    public Optional<User> findById(long id) {
        final String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, this::makeLocalUser, id);
        return users.size() == 0 ?
                Optional.empty() :
                Optional.ofNullable(users.get(0));
    }

    @Override
    public List<User> findAll() {
        final String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::makeLocalUser);
    }

    @Override
    public void deleteById(long id) {
        final String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private User makeLocalUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
                (rs.getDate("BIRTHDAY")).toLocalDate());
    }
}
