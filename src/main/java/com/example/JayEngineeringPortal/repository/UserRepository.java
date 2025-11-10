package com.example.JayEngineeringPortal.repository;




import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.JayEngineeringPortal.model.User;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        List<User> users = jdbcTemplate.query(sql, new Object[]{username}, (rs, rowNum) -> mapUser(rs));

        return users.isEmpty() ? null : users.get(0);
    }

    public int save(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRole());
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId((int) rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
