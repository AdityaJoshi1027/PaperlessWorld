package com.archive.paperlessworld.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentAnalysisServiceTest {

    private DocumentAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new DocumentAnalysisService();
    }

    @Test
    void testCalculateChecksum() {
        String content = "Hello World";
        String checksum = analysisService.calculateChecksum(content.getBytes());
        
        assertNotNull(checksum);
        // SHA-256 of "Hello World" is known
        // a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e
        assertEquals("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e", checksum);
    }

    @Test
    void testExtractKeywords() {
        String text = "The quick brown fox jumps over the lazy dog. The fox is quick.";
        List<String> keywords = analysisService.extractKeywords(text);
        
        assertNotNull(keywords);
        assertFalse(keywords.isEmpty());
        
        // "quick" and "fox" appear twice, should be top keywords
        assertTrue(keywords.contains("quick"));
        assertTrue(keywords.contains("fox"));
        
        // Stop words like "the", "is" should be ignored
        assertFalse(keywords.contains("the"));
        assertFalse(keywords.contains("is"));
    }

    @Test
    void testExtractKeywordsEmpty() {
        List<String> keywords = analysisService.extractKeywords("");
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }
}
