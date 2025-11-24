package com.archive.paperlessworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaperlessWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperlessWorldApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "  📚 Paperless World - Started!\n" +
                "  Digital Archive System\n" +
                "========================================\n" +
                "  API: http://localhost:8080\n" +
                "  Health: http://localhost:8080/api/health\n" +
                "========================================\n");
    }
}
