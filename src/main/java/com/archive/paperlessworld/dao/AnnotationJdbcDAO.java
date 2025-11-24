package com.archive.paperlessworld.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.archive.paperlessworld.model.Annotation;

/**
 * JDBC DAO for Annotation operations
 * Demonstrates traditional JDBC with PreparedStatement, ResultSet, and connection management
 */
@Repository
public class AnnotationJdbcDAO {

    private final DataSource dataSource;

    @Autowired
    public AnnotationJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Find annotation by ID
     */
    public Optional<Annotation> findById(String id) {
        String sql = "SELECT * FROM annotations WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAnnotation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding annotation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Optional.empty();
    }

    /**
     * Find all annotations for a specific document
     */
    public List<Annotation> findByDocumentId(String documentId) {
        String sql = "SELECT * FROM annotations WHERE document_id = ? ORDER BY created_at DESC";
        List<Annotation> annotations = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(documentId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    annotations.add(mapResultSetToAnnotation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding annotations by document ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return annotations;
    }

    /**
     * Find all annotations by a specific user
     */
    public List<Annotation> findByUserId(String userId) {
        String sql = "SELECT * FROM annotations WHERE user_id = ? ORDER BY created_at DESC";
        List<Annotation> annotations = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(userId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    annotations.add(mapResultSetToAnnotation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding annotations by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return annotations;
    }

    /**
     * Find all annotations
     */
    public List<Annotation> findAll() {
        String sql = "SELECT * FROM annotations ORDER BY created_at DESC";
        List<Annotation> annotations = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                annotations.add(mapResultSetToAnnotation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all annotations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return annotations;
    }

    /**
     * Save annotation (insert or update)
     * Demonstrates explicit transaction management with commit/rollback
     */
    public Annotation save(Annotation annotation) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            if (annotation.getId() == null || annotation.getId().isEmpty()) {
                // INSERT new annotation
                String sql = "INSERT INTO annotations (document_id, user_id, content, page_number, " +
                           "position_x, position_y, annotation_type, created_at, updated_at) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setLong(1, Long.parseLong(annotation.getDocumentId()));
                pstmt.setLong(2, Long.parseLong(annotation.getUserId()));
                pstmt.setString(3, annotation.getContent());
                pstmt.setInt(4, annotation.getPage() != null ? annotation.getPage() : 0);
                pstmt.setDouble(5, annotation.getPositionX() != null ? annotation.getPositionX() : 0.0);
                pstmt.setDouble(6, annotation.getPositionY() != null ? annotation.getPositionY() : 0.0);
                pstmt.setString(7, annotation.getType());
                pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("Creating annotation failed, no rows affected.");
                }
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        annotation.setId(String.valueOf(generatedKeys.getLong(1)));
                    } else {
                        conn.rollback();
                        throw new SQLException("Creating annotation failed, no ID obtained.");
                    }
                }
            } else {
                // UPDATE existing annotation
                String sql = "UPDATE annotations SET content = ?, page_number = ?, position_x = ?, " +
                           "position_y = ?, annotation_type = ?, updated_at = ? WHERE id = ?";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, annotation.getContent());
                pstmt.setInt(2, annotation.getPage() != null ? annotation.getPage() : 0);
                pstmt.setDouble(3, annotation.getPositionX() != null ? annotation.getPositionX() : 0.0);
                pstmt.setDouble(4, annotation.getPositionY() != null ? annotation.getPositionY() : 0.0);
                pstmt.setString(5, annotation.getType());
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setLong(7, Long.parseLong(annotation.getId()));
                
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            return annotation;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error saving annotation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save annotation", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Delete annotation by ID
     */
    public boolean deleteById(String id) {
        String sql = "DELETE FROM annotations WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting annotation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count annotations for a document
     */
    public long countByDocumentId(String documentId) {
        String sql = "SELECT COUNT(*) FROM annotations WHERE document_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(documentId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting annotations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Annotation object
     */
    private Annotation mapResultSetToAnnotation(ResultSet rs) throws SQLException {
        Annotation annotation = new Annotation();
        annotation.setId(String.valueOf(rs.getLong("id")));
        annotation.setDocumentId(String.valueOf(rs.getLong("document_id")));
        annotation.setUserId(String.valueOf(rs.getLong("user_id")));
        annotation.setContent(rs.getString("content"));
        annotation.setPage(rs.getInt("page_number"));
        annotation.setPositionX(rs.getDouble("position_x"));
        annotation.setPositionY(rs.getDouble("position_y"));
        annotation.setType(rs.getString("annotation_type"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            annotation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            annotation.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return annotation;
    }
}
