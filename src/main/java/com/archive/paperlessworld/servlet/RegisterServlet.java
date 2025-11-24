package com.archive.paperlessworld.servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.archive.paperlessworld.dao.UserJdbcDAO;
import com.archive.paperlessworld.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Traditional Servlet for user registration
 * Demonstrates HttpServlet extension, @WebServlet annotation,
 * JSP forwarding using RequestDispatcher
 */
@Component
@WebServlet(name = "RegisterServlet", urlPatterns = {"/servlet/register"})
public class RegisterServlet extends HttpServlet {

    @Autowired
    private UserJdbcDAO userJdbcDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Handle GET requests - Forward to registration JSP page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Forward to register JSP page
        request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
    }

    /**
     * Handle POST requests - Process registration
     * Demonstrates validation, JDBC DAO interaction, and RequestDispatcher
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Name is required");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }
        
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            request.setAttribute("errorMessage", "Valid email is required");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }
        
        if (role == null || (!role.equals("public") && !role.equals("researcher") && !role.equals("archivist"))) {
            role = "public"; // Default role
        }
        
        try {
            // Check if user already exists
            Optional<User> existingUser = userJdbcDAO.findByEmail(email.trim().toLowerCase());
            if (existingUser.isPresent()) {
                request.setAttribute("errorMessage", "Email already registered");
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setName(name.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPassword(passwordEncoder.encode(password)); // Hash password
            newUser.setRole(role);
            newUser.setStatus("approved"); // Auto-approve for demo purposes
            newUser.setAccessLevel(role.equals("archivist") ? "full" : (role.equals("researcher") ? "restricted" : "public"));
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            
            // Save to database using JDBC DAO
            userJdbcDAO.save(newUser);
            
            // Set success message and redirect to login
            request.setAttribute("successMessage", "Registration successful! Please login with your credentials.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }

    /**
     * Helper method to validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Get servlet information
     */
    @Override
    public String getServletInfo() {
        return "Register Servlet for Paperless World - Traditional HttpServlet implementation with JSP";
    }
}
