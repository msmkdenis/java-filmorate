package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaStorageDaoImpl implements MpaStorageDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> findById(int id) {
        final String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        final List<Mpa> mpa = jdbcTemplate.query(sqlQuery, this::makeLocalMpa, id);
        return mpa.size() == 0 ?
                Optional.empty() :
                Optional.ofNullable(mpa.get(0));
    }

    private Mpa makeLocalMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getInt("MPA_ID"),
                rs.getString("MPA_NAME")
        );
    }

    @Override
    public List<Mpa> findAll() {
        final String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::makeLocalMpa);
    }
}
