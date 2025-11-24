package com.archive.paperlessworld.servlet;

import java.io.IOException;
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
import jakarta.servlet.http.HttpSession;

/**
 * Traditional Servlet for user login
 * Demonstrates HttpServlet extension, @WebServlet annotation,
 * doGet() and doPost() methods, HttpSession management,
 * and JSP forwarding using RequestDispatcher
 */
@Component
@WebServlet(name = "LoginServlet", urlPatterns = {"/servlet/login"})
public class LoginServlet extends HttpServlet {

    @Autowired
    private UserJdbcDAO userJdbcDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Handle GET requests - Forward to login JSP page
     * Demonstrates RequestDispatcher usage for JSP forwarding
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Already logged in, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/servlet/dashboard");
            return;
        }
        
        // Forward to login JSP page
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Handle POST requests - Process login authentication
     * Demonstrates HttpServletRequest parameter reading,
     * HttpSession creation, and redirect operations
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get form parameters (traditional servlet approach)
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate input
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Find user by email using JDBC DAO
            Optional<User> userOptional = userJdbcDAO.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                request.setAttribute("errorMessage", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            
            User user = userOptional.get();
            
            // Verify password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                request.setAttribute("errorMessage", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            
            // Check user status
            if (!"approved".equalsIgnoreCase(user.getStatus())) {
                request.setAttribute("errorMessage", "Account pending approval");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            
            // Create HTTP Session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(3600); // 1 hour
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/servlet/dashboard");
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }

    /**
     * Get servlet information
     */
    @Override
    public String getServletInfo() {
        return "Login Servlet for Paperless World - Traditional HttpServlet implementation";
    }
}
