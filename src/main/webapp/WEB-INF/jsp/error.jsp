<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Paperless World</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .error-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            max-width: 600px;
            width: 100%;
            padding: 50px;
            text-align: center;
        }
        
        .error-icon {
            font-size: 80px;
            margin-bottom: 20px;
        }
        
        h1 {
            color: #c33;
            font-size: 32px;
            margin-bottom: 15px;
        }
        
        .error-message {
            color: #666;
            font-size: 18px;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 30px;
            text-align: left;
        }
        
        .error-details h3 {
            color: #333;
            margin-bottom: 10px;
            font-size: 16px;
        }
        
        .error-details p {
            color: #666;
            font-size: 14px;
            line-height: 1.6;
        }
        
        .error-code {
            font-family: monospace;
            background: #fff;
            padding: 10px;
            border-radius: 3px;
            margin-top: 10px;
            color: #c33;
            word-break: break-all;
        }
        
        .btn-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 12px 30px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 600;
            transition: transform 0.2s;
            display: inline-block;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        
        <h1>Oops! Something Went Wrong</h1>
        
        <p class="error-message">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    ${errorMessage}
                </c:when>
                <c:otherwise>
                    An unexpected error occurred while processing your request.
                </c:otherwise>
            </c:choose>
        </p>
        
        <c:if test="${not empty errorDetails}">
            <div class="error-details">
                <h3>Error Details:</h3>
                <p>${errorDetails}</p>
                
                <c:if test="${not empty errorCode}">
                    <div class="error-code">
                        Error Code: ${errorCode}
                    </div>
                </c:if>
            </div>
        </c:if>
        
        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/servlet/dashboard" class="btn btn-primary">
                🏠 Go to Dashboard
            </a>
            
            <a href="javascript:history.back()" class="btn btn-secondary">
                ← Go Back
            </a>
        </div>
        
        <p style="margin-top: 30px; color: #999; font-size: 12px;">
            If this problem persists, please contact the system administrator.
        </p>
    </div>
</body>
</html>
