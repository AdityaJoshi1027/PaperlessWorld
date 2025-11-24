package com.archive.paperlessworld.servlet;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.archive.paperlessworld.dao.AnnotationJdbcDAO;
import com.archive.paperlessworld.dao.DocumentJdbcDAO;
import com.archive.paperlessworld.dao.FeedbackJdbcDAO;
import com.archive.paperlessworld.dao.UserJdbcDAO;
import com.archive.paperlessworld.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Dashboard Servlet - Main application dashboard
 * Demonstrates session management, JSP forwarding,
 * and JDBC DAO usage for statistics
 */
@Component
@WebServlet(name = "DashboardServlet", urlPatterns = {"/servlet/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Autowired
    private UserJdbcDAO userJdbcDAO;

    @Autowired
    private DocumentJdbcDAO documentJdbcDAO;

    @Autowired
    private AnnotationJdbcDAO annotationJdbcDAO;

    @Autowired
    private FeedbackJdbcDAO feedbackJdbcDAO;

    /**
     * Handle GET requests - Display dashboard
     * Demonstrates HttpSession validation and JSP forwarding with attributes
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/servlet/login");
            return;
        }
        
        // Get user from session
        User user = (User) session.getAttribute("user");
        
        try {
            // Fetch statistics using JDBC DAOs
            // Count all records by getting list size (simpler than adding count() methods)
            long totalUsers = userJdbcDAO.findAll().size();
            long totalDocuments = documentJdbcDAO.findAll().size();
            long totalAnnotations = annotationJdbcDAO.findAll().size();
            long totalFeedback = feedbackJdbcDAO.count(); // FeedbackJdbcDAO has count() method
            
            // Get user-specific stats
            long userDocuments = documentJdbcDAO.countByUploader(user.getId());
            long userAnnotations = annotationJdbcDAO.findByUserId(user.getId()).size();
            
            // Set attributes for JSP
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalDocuments", totalDocuments);
            request.setAttribute("totalAnnotations", totalAnnotations);
            request.setAttribute("totalFeedback", totalFeedback);
            request.setAttribute("userDocuments", userDocuments);
            request.setAttribute("userAnnotations", userAnnotations);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Error occurred, set error message and forward to error page
            request.setAttribute("errorMessage", "Failed to load dashboard");
            request.setAttribute("errorDetails", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Handle POST requests - Same as GET
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Get servlet information
     */
    @Override
    public String getServletInfo() {
        return "Dashboard Servlet - Main application dashboard with JDBC statistics";
    }
}
