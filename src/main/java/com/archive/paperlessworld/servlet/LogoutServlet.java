package com.archive.paperlessworld.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Traditional Servlet for user logout
 * Demonstrates HttpSession invalidation and session management
 */
@Component
@WebServlet(name = "LogoutServlet", urlPatterns = {"/servlet/logout"})
public class LogoutServlet extends HttpServlet {

    /**
     * Handle GET requests - Logout user and invalidate session
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get existing session (don't create new one)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Get user info before invalidating
            String userEmail = (String) session.getAttribute("userEmail");
            
            // Invalidate session
            session.invalidate();
            
            // Send HTML response
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            try {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Logged Out - Paperless World</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; margin: 40px; text-align: center; }");
                out.println("a { color: #007bff; text-decoration: none; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Logged Out Successfully</h1>");
                out.println("<p>You have been logged out from account: " + (userEmail != null ? userEmail : "Unknown") + "</p>");
                out.println("<p><a href='/servlet/login'>Login again</a></p>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        } else {
            // No session found
            response.sendRedirect("/servlet/login");
        }
    }

    /**
     * Handle POST requests - Also support POST for logout
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get existing session
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
        
        // Send JSON response
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("{\"success\": true, \"message\": \"Logged out successfully\"}");
        } finally {
            out.close();
        }
    }

    @Override
    public String getServletInfo() {
        return "Logout Servlet for Paperless World - Session management demonstration";
    }
}
