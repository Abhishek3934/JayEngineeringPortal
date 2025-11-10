package com.example.JayEngineeringPortal.dao;

import com.example.JayEngineeringPortal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbc;

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> list = jdbc.query(sql, new Object[]{username}, (rs, rowNum) -> mapUser(rs));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int save(User u) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        return jdbc.update(sql, u.getUsername(), u.getPassword(), u.getRole());
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
