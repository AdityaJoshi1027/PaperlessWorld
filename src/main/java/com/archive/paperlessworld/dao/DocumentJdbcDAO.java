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

import com.archive.paperlessworld.model.ArchiveDocument;

/**
 * JDBC DAO for ArchiveDocument entity
 * Demonstrates traditional JDBC operations with explicit connection management,
 * PreparedStatements, ResultSet mapping, and transaction handling
 */
@Repository
public class DocumentJdbcDAO {

    private final DataSource dataSource;

    @Autowired
    public DocumentJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Find ArchiveDocument by ID using JDBC PreparedStatement
     */
    public Optional<ArchiveDocument> findById(String id) {
        String sql = "SELECT id, title, description, file_name, file_path, file_type, file_size, " +
                     "uploader_id, category, tags, access_level, created_at, updated_at " +
                     "FROM documents WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ArchiveDocument by ID: " + id, e);
        }
        
        return Optional.empty();
    }

    /**
     * Find all documents using JDBC Statement
     */
    public List<ArchiveDocument> findAll() {
        String sql = "SELECT id, title, description, file_name, file_path, file_type, file_size, " +
                     "uploader_id, category, tags, access_level, created_at, updated_at " +
                     "FROM documents ORDER BY created_at DESC";
        List<ArchiveDocument> documents = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all documents", e);
        }
        
        return documents;
    }

    /**
     * Find documents by uploader ID using JDBC PreparedStatement
     */
    public List<ArchiveDocument> findByUploaderId(String uploaderId) {
        String sql = "SELECT id, title, description, file_name, file_path, file_type, file_size, " +
                     "uploader_id, category, tags, access_level, created_at, updated_at " +
                     "FROM documents WHERE uploader_id = ? ORDER BY created_at DESC";
        List<ArchiveDocument> documents = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(uploaderId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding documents by uploader: " + uploaderId, e);
        }
        
        return documents;
    }

    /**
     * Find documents by category using JDBC PreparedStatement
     */
    public List<ArchiveDocument> findByCategory(String category) {
        String sql = "SELECT id, title, description, file_name, file_path, file_type, file_size, " +
                     "uploader_id, category, tags, access_level, created_at, updated_at " +
                     "FROM documents WHERE category = ? ORDER BY created_at DESC";
        List<ArchiveDocument> documents = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding documents by category: " + category, e);
        }
        
        return documents;
    }

    /**
     * Search documents by title using JDBC PreparedStatement with LIKE clause
     */
    public List<ArchiveDocument> searchByTitle(String searchTerm) {
        String sql = "SELECT id, title, description, file_name, file_path, file_type, file_size, " +
                     "uploader_id, category, tags, access_level, created_at, updated_at " +
                     "FROM documents WHERE title LIKE ? ORDER BY created_at DESC";
        List<ArchiveDocument> documents = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching documents by title: " + searchTerm, e);
        }
        
        return documents;
    }

    /**
     * Save ArchiveDocument with explicit transaction management
     * Demonstrates commit/rollback for ACID compliance
     */
    public ArchiveDocument save(ArchiveDocument ArchiveDocument) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dataSource.getConnection();
            // Disable auto-commit for explicit transaction control
            conn.setAutoCommit(false);
            
            if (ArchiveDocument.getId() == null) {
                // INSERT new ArchiveDocument
                String sql = "INSERT INTO documents (title, description, file_name, file_path, file_type, " +
                            "file_size, uploader_id, category, tags, access_level) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, ArchiveDocument.getTitle());
                pstmt.setString(2, ArchiveDocument.getDescription());
                pstmt.setString(3, ArchiveDocument.getFileName());
                pstmt.setString(4, ArchiveDocument.getFilePath());
                pstmt.setString(5, ArchiveDocument.getMimeType());
                pstmt.setLong(6, ArchiveDocument.getFileSize() != null ? ArchiveDocument.getFileSize() : 0L);
                pstmt.setLong(7, ArchiveDocument.getUploadedBy() != null ? Long.parseLong(ArchiveDocument.getUploadedBy().getId()) : 0L);
                pstmt.setString(8, ArchiveDocument.getCategory());
                pstmt.setString(9, ArchiveDocument.getTags() != null ? String.join(",", ArchiveDocument.getTags()) : null);
                pstmt.setString(10, ArchiveDocument.getAccessLevel() != null ? ArchiveDocument.getAccessLevel() : "public");
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating ArchiveDocument failed, no rows affected");
                }
                
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    ArchiveDocument.setId(String.valueOf(rs.getLong(1)));
                }
            } else {
                // UPDATE existing ArchiveDocument
                String sql = "UPDATE documents SET title = ?, description = ?, file_name = ?, file_path = ?, " +
                            "file_type = ?, file_size = ?, category = ?, tags = ?, access_level = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, ArchiveDocument.getTitle());
                pstmt.setString(2, ArchiveDocument.getDescription());
                pstmt.setString(3, ArchiveDocument.getFileName());
                pstmt.setString(4, ArchiveDocument.getFilePath());
                pstmt.setString(5, ArchiveDocument.getMimeType());
                pstmt.setLong(6, ArchiveDocument.getFileSize() != null ? ArchiveDocument.getFileSize() : 0L);
                pstmt.setString(7, ArchiveDocument.getCategory());
                pstmt.setString(8, ArchiveDocument.getTags() != null ? String.join(",", ArchiveDocument.getTags()) : null);
                pstmt.setString(9, ArchiveDocument.getAccessLevel());
                pstmt.setLong(10, Long.parseLong(ArchiveDocument.getId()));
                
                pstmt.executeUpdate();
            }
            
            // Commit transaction
            conn.commit();
            return ArchiveDocument;
            
        } catch (SQLException e) {
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error saving ArchiveDocument", e);
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
     * Delete ArchiveDocument by ID with transaction management
     */
    public boolean deleteById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "DELETE FROM documents WHERE id = ?";
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
            throw new RuntimeException("Error deleting ArchiveDocument", e);
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
     * Count documents by uploader using JDBC
     */
    public int countByUploader(String uploaderId) {
        String sql = "SELECT COUNT(*) FROM documents WHERE uploader_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(uploaderId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting documents by uploader: " + uploaderId, e);
        }
        
        return 0;
    }

    /**
     * Get total size of documents by uploader
     */
    public long getTotalSizeByUploader(String uploaderId) {
        String sql = "SELECT COALESCE(SUM(file_size), 0) FROM documents WHERE uploader_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, Long.parseLong(uploaderId));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total size by uploader: " + uploaderId, e);
        }
        
        return 0;
    }

    /**
     * Map ResultSet to ArchiveDocument entity
     */
    private ArchiveDocument mapResultSetToDocument(ResultSet rs) throws SQLException {
        ArchiveDocument document = new ArchiveDocument();
        document.setId(String.valueOf(rs.getLong("id")));
        document.setTitle(rs.getString("title"));
        document.setDescription(rs.getString("description"));
        document.setFileName(rs.getString("file_name"));
        document.setFilePath(rs.getString("file_path"));
        document.setMimeType(rs.getString("file_type"));
        document.setFileSize(rs.getLong("file_size"));
        // Note: uploadedBy is User object, skip for JDBC demo
        document.setCategory(rs.getString("category"));
        
        // Tags are stored as comma-separated string
        document.setTags(rs.getString("tags"));
        
        document.setAccessLevel(rs.getString("access_level"));
        
        // Handle timestamps
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            document.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            document.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        return document;
    }
}
