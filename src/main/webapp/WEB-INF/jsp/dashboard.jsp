<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.archive.paperlessworld.model.User" %>
<%
    // Check if user is logged in
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/servlet/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Paperless World</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .navbar-brand {
            font-size: 24px;
            font-weight: bold;
        }
        
        .navbar-user {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-info {
            text-align: right;
        }
        
        .user-name {
            font-weight: 600;
        }
        
        .user-role {
            font-size: 12px;
            opacity: 0.9;
            text-transform: uppercase;
        }
        
        .btn-logout {
            padding: 8px 20px;
            background: rgba(255,255,255,0.2);
            border: 2px solid white;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-logout:hover {
            background: white;
            color: #667eea;
        }
        
        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
        
        .welcome-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .welcome-card h1 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .welcome-card p {
            color: #666;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border-left: 4px solid #667eea;
        }
        
        .stat-card h3 {
            color: #666;
            font-size: 14px;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        
        .stat-card .stat-value {
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
        }
        
        .quick-actions {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .quick-actions h2 {
            color: #333;
            margin-bottom: 20px;
        }
        
        .actions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .action-btn {
            display: block;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            text-align: center;
            font-weight: 600;
            transition: transform 0.2s;
        }
        
        .action-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .footer {
            text-align: center;
            padding: 20px;
            color: #666;
            margin-top: 50px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-brand">
            📚 Paperless World
        </div>
        <div class="navbar-user">
            <div class="user-info">
                <div class="user-name"><%= user.getName() %></div>
                <div class="user-role"><%= user.getRole() %></div>
            </div>
            <a href="${pageContext.request.contextPath}/servlet/logout" class="btn-logout">Logout</a>
        </div>
    </nav>
    
    <div class="container">
        <div class="welcome-card">
            <h1>Welcome back, <%= user.getName() %>! 👋</h1>
            <p>Manage your digital archive efficiently with Paperless World</p>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total Documents</h3>
                <div class="stat-value">${totalDocuments != null ? totalDocuments : 0}</div>
            </div>
            
            <div class="stat-card">
                <h3>Your Uploads</h3>
                <div class="stat-value">${userDocuments != null ? userDocuments : 0}</div>
            </div>
            
            <div class="stat-card">
                <h3>Annotations</h3>
                <div class="stat-value">${totalAnnotations != null ? totalAnnotations : 0}</div>
            </div>
            
            <div class="stat-card">
                <h3>Active Users</h3>
                <div class="stat-value">${activeUsers != null ? activeUsers : 0}</div>
            </div>
        </div>
        
        <div class="quick-actions">
            <h2>Quick Actions</h2>
            <div class="actions-grid">
                <a href="${pageContext.request.contextPath}/servlet/documents" class="action-btn">
                    📄 View Documents
                </a>
                
                <c:if test="${sessionScope.user.role == 'researcher' || sessionScope.user.role == 'archivist' || sessionScope.user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/servlet/upload" class="action-btn">
                        ⬆️ Upload Document
                    </a>
                </c:if>
                
                <a href="${pageContext.request.contextPath}/servlet/search" class="action-btn">
                    🔍 Search Archive
                </a>
                
                <c:if test="${sessionScope.user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/servlet/users" class="action-btn">
                        👥 Manage Users
                    </a>
                </c:if>
                
                <a href="${pageContext.request.contextPath}/api/async/stats" class="action-btn">
                    📊 System Stats
                </a>
                
                <a href="${pageContext.request.contextPath}/servlet/profile" class="action-btn">
                    ⚙️ My Profile
                </a>
            </div>
        </div>
    </div>
    
    <div class="footer">
        <p>© 2025 Paperless World - Digital Archive System | Team Double A</p>
        <p style="font-size: 12px; margin-top: 5px;">Built with JDBC + Servlets + JSP</p>
    </div>
</body>
</html>
