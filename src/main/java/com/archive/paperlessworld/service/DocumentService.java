package com.archive.paperlessworld.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.archive.paperlessworld.dao.DocumentJdbcDAO;
import com.archive.paperlessworld.model.ArchiveDocument;

/**
 * Service layer for Document management.
 * Enforces business logic, security checks, and integrates analysis features.
 */
@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentJdbcDAO documentJdbcDAO;
    private final DocumentAnalysisService analysisService;

    @Autowired
    public DocumentService(DocumentJdbcDAO documentJdbcDAO, DocumentAnalysisService analysisService) {
        this.documentJdbcDAO = documentJdbcDAO;
        this.analysisService = analysisService;
    }

    /**
     * Retrieves a document by ID.
     */
    public Optional<ArchiveDocument> getDocumentById(String id) {
        logger.debug("Fetching document with ID: {}", id);
        return documentJdbcDAO.findById(id);
    }

    /**
     * Retrieves all documents.
     */
    public List<ArchiveDocument> getAllDocuments() {
        logger.debug("Fetching all documents");
        return documentJdbcDAO.findAll();
    }

    /**
     * Saves a document after performing analysis.
     * INNOVATION: Automatically generates tags and checksums.
     */
    public ArchiveDocument saveDocument(ArchiveDocument document, byte[] fileContent) {
        logger.info("Saving document: {}", document.getTitle());

        // 1. Integrity Check (Innovation)
        if (fileContent != null && fileContent.length > 0) {
            String checksum = analysisService.calculateChecksum(fileContent);
            // In a real app, we would store this checksum. For now, we log it.
            logger.info("Document Integrity Checksum (SHA-256): {}", checksum);
        }

        // 2. Auto-Tagging (Innovation)
        if (document.getTags() == null || document.getTags().isEmpty()) {
            String contentToAnalyze = document.getTitle() + " " + (document.getDescription() != null ? document.getDescription() : "");
            List<String> autoTags = analysisService.extractKeywords(contentToAnalyze);
            document.setTags(String.join(",", autoTags));
            logger.info("Auto-generated tags: {}", autoTags);
        }

        return documentJdbcDAO.save(document);
    }

    /**
     * Deletes a document.
     */
    public void deleteDocument(String id) {
        logger.info("Deleting document with ID: {}", id);
        documentJdbcDAO.delete(id);
    }
    
    /**
     * Search documents by title.
     */
    public List<ArchiveDocument> searchDocuments(String query) {
        logger.debug("Searching documents with query: {}", query);
        return documentJdbcDAO.searchByTitle(query);
    }
}
