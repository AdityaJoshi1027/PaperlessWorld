package com.archive.paperlessworld.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.archive.paperlessworld.exception.DatabaseOperationException;
import com.archive.paperlessworld.model.User;

/**
 * JDBC DAO for User entity
 * Demonstrates traditional JDBC operations with explicit connection management,
 * PreparedStatements, ResultSet mapping, and transaction handling
 */
@Repository
public class UserJdbcDAO {

    private final DataSource dataSource;

    @Autowired
    public UserJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Find user by ID using JDBC PreparedStatement
     */
    public Optional<User> findById(String id) {
        String sql = "SELECT id, name, email, password, role, status, access_level, created_at, updated_at FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw DatabaseOperationException.selectFailed("User", e);
        }
        
        return Optional.empty();
    }

    /**
     * Find user by email using JDBC PreparedStatement
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role, status, access_level, created_at, updated_at FROM users WHERE email = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw DatabaseOperationException.selectFailed("User", e);
        }
        
        return Optional.empty();
    }

    /**
     * Find all users using JDBC Statement
     */
    public List<User> findAll() {
        String sql = "SELECT id, name, email, password, role, status, access_level, created_at, updated_at FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw DatabaseOperationException.selectFailed("Users", e);
        }
        
        return users;
    }

    /**
     * Find users by role using JDBC PreparedStatement
     */
    public List<User> findByRole(String role) {
        String sql = "SELECT id, name, email, password, role, status, access_level, created_at, updated_at FROM users WHERE role = ? ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by role: " + role, e);
        }
        
        return users;
    }

    /**
     * Save user with explicit transaction management
     * Demonstrates commit/rollback for ACID compliance
     */
    public User save(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dataSource.getConnection();
            // Disable auto-commit for explicit transaction control
            conn.setAutoCommit(false);
            
            if (user.getId() == null) {
                // INSERT new user
                String sql = "INSERT INTO users (name, email, password, role, status, access_level) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setString(4, user.getRole());
                pstmt.setString(5, user.getStatus() != null ? user.getStatus() : "pending");
                pstmt.setString(6, user.getAccessLevel() != null ? user.getAccessLevel() : "public");
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected");
                }
                
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(String.valueOf(rs.getLong(1)));
                }
            } else {
                // UPDATE existing user
                String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, status = ?, access_level = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setString(4, user.getRole());
                pstmt.setString(5, user.getStatus());
                pstmt.setString(6, user.getAccessLevel());
                pstmt.setLong(7, Long.parseLong(user.getId()));
                
                pstmt.executeUpdate();
            }
            
            // Commit transaction
            conn.commit();
            return user;
            
        } catch (SQLException e) {
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error saving user", e);
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error closing resources", e);
            }
        }
    }

    /**
     * Update user status with transaction management
     */
    public boolean updateStatus(String userId, String status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE users SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setLong(2, Long.parseLong(userId));
            
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error updating user status", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error closing resources", e);
            }
        }
    }

    /**
     * Delete user by ID with transaction management
     */
    public boolean deleteById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "DELETE FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(id));
            
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error deleting user", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error closing resources", e);
            }
        }
    }

    /**
     * Count users by status using JDBC
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM users WHERE status = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting users by status: " + status, e);
        }
        
        return 0;
    }

    /**
     * Map ResultSet to User entity
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(String.valueOf(rs.getLong("id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setAccessLevel(rs.getString("access_level"));
        
        // Handle timestamps
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            user.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            user.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        return user;
    }
}
