package com.archive.paperlessworld.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for advanced document analysis.
 * INNOVATION FEATURE: Implements content analysis and integrity verification.
 */
@Service
public class DocumentAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAnalysisService.class);
    private static final List<String> STOP_WORDS = Arrays.asList(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", 
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at"
    );

    /**
     * Calculates SHA-256 checksum for file integrity verification.
     * This ensures that archived documents have not been tampered with.
     * 
     * @param data The file content in bytes
     * @return Hexadecimal string of the checksum
     */
    public String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            logger.debug("Calculated checksum for {} bytes", data.length);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("Integrity check failed: Algorithm not found", e);
        }
    }

    /**
     * Extracts keywords from text using frequency analysis.
     * Simulates AI-based tagging for better searchability.
     * 
     * @param text The text to analyze (title + description)
     * @return List of top 5 keywords
     */
    public List<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        // Normalize text
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");

        // Calculate frequency
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            if (word.length() > 3 && !STOP_WORDS.contains(word)) {
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }
        }

        // Sort by frequency and take top 5
        return frequencyMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
