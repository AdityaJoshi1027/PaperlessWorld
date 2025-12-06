package com.archive.paperlessworld.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.archive.paperlessworld.exception.DatabaseOperationException;
import com.archive.paperlessworld.model.Feedback;

/**
 * JDBC DAO for Feedback operations
 * Demonstrates traditional JDBC with PreparedStatement, ResultSet, and transaction management
 */
@Repository
public class FeedbackJdbcDAO {

    private final DataSource dataSource;

    @Autowired
    public FeedbackJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Find feedback by ID
     */
    public Optional<Feedback> findById(String id) {
        String sql = "SELECT * FROM feedback WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding feedback by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }

    /**
     * Find all feedback by user ID
     */
    public List<Feedback> findByUserId(String userId) {
        String sql = "SELECT * FROM feedback WHERE user_id = ? ORDER BY created_at DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(userId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding feedback by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return feedbackList;
    }

    /**
     * Find all feedback by document ID
     */
    public List<Feedback> findByDocumentId(String documentId) {
        String sql = "SELECT * FROM feedback WHERE document_id = ? ORDER BY created_at DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(documentId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding feedback by document ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return feedbackList;
    }

    /**
     * Find all feedback by status
     */
    public List<Feedback> findByStatus(String status) {
        String sql = "SELECT * FROM feedback WHERE status = ? ORDER BY created_at DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding feedback by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return feedbackList;
    }

    /**
     * Find all feedback
     */
    public List<Feedback> findAll() {
        String sql = "SELECT * FROM feedback ORDER BY created_at DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all feedback: " + e.getMessage());
            e.printStackTrace();
        }
        
        return feedbackList;
    }

    /**
     * Save feedback (insert or update)
     * Demonstrates explicit transaction management with commit/rollback
     */
    public Feedback save(Feedback feedback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            if (feedback.getId() == null || feedback.getId().isEmpty()) {
                // INSERT new feedback
                String sql = "INSERT INTO feedback (user_id, document_id, subject, message, status, " +
                           "priority, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setLong(1, Long.parseLong(feedback.getUserId()));
                
                // document_id can be null
                if (feedback.getDocumentId() != null && !feedback.getDocumentId().isEmpty()) {
                    pstmt.setLong(2, Long.parseLong(feedback.getDocumentId()));
                } else {
                    pstmt.setNull(2, Types.BIGINT);
                }
                
                pstmt.setString(3, feedback.getSubject());
                pstmt.setString(4, feedback.getMessage());
                pstmt.setString(5, feedback.getStatus() != null ? feedback.getStatus() : "pending");
                pstmt.setString(6, feedback.getPriority() != null ? feedback.getPriority() : "normal");
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("Creating feedback failed, no rows affected.");
                }
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        feedback.setId(String.valueOf(generatedKeys.getLong(1)));
                    } else {
                        conn.rollback();
                        throw new SQLException("Creating feedback failed, no ID obtained.");
                    }
                }
            } else {
                // UPDATE existing feedback
                String sql = "UPDATE feedback SET subject = ?, message = ?, status = ?, " +
                           "priority = ?, updated_at = ? WHERE id = ?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, feedback.getSubject());
                pstmt.setString(2, feedback.getMessage());
                pstmt.setString(3, feedback.getStatus());
                pstmt.setString(4, feedback.getPriority());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setLong(6, Long.parseLong(feedback.getId()));
                
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            return feedback;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    throw DatabaseOperationException.rollbackFailed(ex);
                }
            }
            System.err.println("Error saving feedback: " + e.getMessage());
            e.printStackTrace();
            if (feedback.getId() == null) {
                throw DatabaseOperationException.insertFailed("Feedback", e);
            } else {
                throw DatabaseOperationException.updateFailed("Feedback", feedback.getId(), e);
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new DatabaseOperationException("Error closing resources", e);
            }
        }
    }

    /**
     * Delete feedback by ID
     */
    public boolean deleteById(String id) {
        String sql = "DELETE FROM feedback WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count total feedback
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM feedback";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting feedback: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Feedback object
     */
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(String.valueOf(rs.getLong("id")));
        feedback.setUserId(String.valueOf(rs.getLong("user_id")));
        
        // document_id can be null
        long documentId = rs.getLong("document_id");
        if (!rs.wasNull()) {
            feedback.setDocumentId(String.valueOf(documentId));
        }
        
        feedback.setSubject(rs.getString("subject"));
        feedback.setMessage(rs.getString("message"));
        feedback.setStatus(rs.getString("status"));
        feedback.setPriority(rs.getString("priority"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            feedback.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            feedback.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return feedback;
    }
}
