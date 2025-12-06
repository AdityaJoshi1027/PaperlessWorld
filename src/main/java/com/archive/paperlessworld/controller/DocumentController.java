package com.archive.paperlessworld.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.archive.paperlessworld.model.ArchiveDocument;
import com.archive.paperlessworld.service.DocumentService;

/**
 * REST Controller for Document Management.
 * INNOVATION: Advanced document processing with auto-tagging and checksum verification.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<ArchiveDocument>> getAllDocuments() {
        logger.info("REST Request to get all documents");
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArchiveDocument> getDocument(@PathVariable String id) {
        logger.info("REST Request to get document: {}", id);
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArchiveDocument>> searchDocuments(@RequestParam String query) {
        logger.info("REST Request to search documents with query: {}", query);
        return ResponseEntity.ok(documentService.searchDocuments(query));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ARCHIVIST', 'RESEARCHER')")
    public ResponseEntity<ArchiveDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("accessLevel") String accessLevel) {
        
        logger.info("REST Request to upload document: {}", title);
        
        try {
            ArchiveDocument doc = new ArchiveDocument();
            doc.setTitle(title);
            doc.setDescription(description);
            doc.setCategory(category);
            doc.setAccessLevel(accessLevel);
            doc.setFileName(file.getOriginalFilename());
            doc.setMimeType(file.getContentType());
            doc.setFileSize(file.getSize());
            // In a real app, we'd save the file to disk here and set filePath
            doc.setFilePath("/uploads/" + file.getOriginalFilename()); 
            
            ArchiveDocument savedDoc = documentService.saveDocument(doc, file.getBytes());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDoc);
            
        } catch (IOException e) {
            logger.error("Failed to process file upload", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        logger.info("REST Request to delete document: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
