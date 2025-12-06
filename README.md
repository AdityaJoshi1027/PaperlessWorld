# 📚 Paperless World - Digital Archive System

A full-stack web application for managing digital archives with document storage, annotation capabilities, and role-based access control. Built with modern Java technologies and React for the frontend.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

---

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Demo Accounts](#demo-accounts)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Academic Compliance](#academic-compliance)
- [Troubleshooting](#troubleshooting)

---

## ✨ Features

### Core Functionality
- **Document Management**: Upload, view, and manage digital documents
- **Annotation System**: Add notes and annotations to documents
- **Feedback System**: Submit and manage feedback on documents
- **User Management**: Role-based access control with three user types

### Role-Based Features
- **Archivist (Admin)**: Full system access, user management, document approval
- **Researcher**: Document viewing, annotation, and feedback submission
- **Public**: Limited document viewing and feedback capabilities

### Technical Features
- **JWT Authentication**: Secure token-based authentication
- **BCrypt Password Hashing**: Industry-standard password security
- **RESTful API**: Clean and documented API endpoints
- **Responsive UI**: Modern React-based user interface
- **Multi-threading**: Asynchronous processing with ExecutorService
- **JDBC Integration**: Direct database connectivity without ORM
- **File Upload**: Document upload with validation

### Innovation Features
- **Advanced Document Analysis**: Auto-tagging using keyword extraction algorithm
- **Data Integrity**: SHA-256 Checksum verification for uploaded documents
- **Asynchronous Processing**: Background task execution for performance optimization
- **Modernized Architecture**: Pure REST API backend with React frontend

---

## 🛠 Technology Stack

### Backend
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.5.0**: Application framework
- **Spring Security 6.5.0**: Authentication and authorization
- **JDBC**: Pure database connectivity
- **JWT (JJWT 0.11.5)**: Token-based authentication
- **BCrypt**: Password hashing
- **MySQL Connector**: Database driver
- **Maven**: Dependency management

### Frontend
- **React 18.2.0**: UI library
- **TypeScript**: Type-safe JavaScript
- **React Router 6**: Client-side routing
- **Axios**: HTTP client
- **Lucide React**: Icon library

### Database
- **MySQL 8.0+**: Relational database
- **HikariCP**: Connection pooling

---

## 🏗 Architecture

### System Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                            │
│  ┌──────────────┐                                          │
│  │  React App   │                                          │
│  │  (Port 3000) │                                          │
│  └──────────────┘                                          │
└────────────┬─────────────────────────────────────────────────┘
             │
             │  REST API / HTTP
             │
┌────────────┴─────────────────────────────────────────────────┐
│                    Application Layer                          │
│  ┌────────────────────────────────────────────────────────┐  │
│  │           Spring Boot Application (Port 8080)          │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │  │
│  │  │ Controllers  │  │  Async Tasks │  │  Security   │ │  │
│  │  └──────────────┘  └──────────────┘  └─────────────┘ │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │  │
│  │  │  Services    │  │     DAOs     │  │   Threads   │ │  │
│  │  └──────────────┘  └──────────────┘  └─────────────┘ │  │
│  └────────────────────────────────────────────────────────┘  │
└────────────────────────────┬──────────────────────────────────┘
                             │  JDBC
                             │
┌────────────────────────────┴──────────────────────────────────┐
│                      Data Layer                                │
│                   MySQL Database (Port 3306)                   │
│  ┌──────────┐  ┌───────────┐  ┌────────────┐  ┌───────────┐ │
│  │  Users   │  │ Documents │  │ Annotations│  │ Feedback  │ │
│  └──────────┘  └───────────┘  └────────────┘  └───────────┘ │
└────────────────────────────────────────────────────────────────┘
```

### Key Implementations
This project implements:
- ✅ **JDBC**: Direct database connectivity with PreparedStatement
- ✅ **RESTful API**: Modern API design principles
- ✅ **Multi-threading**: ExecutorService and @Async annotations
- ✅ **Security**: BCrypt password hashing and JWT authentication
- ✅ **MVC Pattern**: Clear separation of concerns
- ✅ **Advanced Analysis**: Auto-tagging and Checksum verification

---

## 📦 Prerequisites

### Required Software
1. **Java Development Kit (JDK) 21**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Apache Maven 3.8+**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **MySQL 8.0+**
   - Download: https://dev.mysql.com/downloads/mysql/
   - Verify: `mysql --version`

4. **Node.js 16+ and npm**
   - Download: https://nodejs.org/
   - Verify: `node -v` and `npm -v`

---

## 🚀 Installation & Setup

### Step 1: Database Setup

1. **Start MySQL Server**
   ```bash
   # Windows
   net start MySQL80
   
   # macOS/Linux
   sudo systemctl start mysql
   ```

2. **Create Database and Tables**
   ```bash
   mysql -u root -p < schema.sql
   ```

3. **Verify Database**
   ```bash
   mysql -u root -p -e "USE paperless_db; SELECT * FROM users;"
   ```
   You should see 3 demo users.

### Step 2: Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update MySQL password if different
spring.datasource.password=root
```

### Step 3: Install Dependencies

```bash
# Install frontend dependencies
cd client
npm install
cd ..

# Build backend
mvn clean package -DskipTests
```

---

## ▶️ Running the Application

### Start Backend (Terminal 1)
```bash
cd paperless-world
java -jar target/paperless-world-1.0.0.jar
```
✅ Backend starts on: **http://localhost:8080**

### Start Frontend (Terminal 2)
```bash
cd paperless-world/client
npm start
```
✅ Frontend starts on: **http://localhost:3000**

### Access Application
Open browser: **http://localhost:3000**

---

## 👥 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@paperless.com | admin123 |
| **Researcher** | researcher@paperless.com | research123 |
| **Public** | public@paperless.com | public123 |

---

## 📁 Project Structure

```
paperless-world/
├── client/                          # React Frontend
│   ├── src/
│   │   ├── components/             # React components
│   │   ├── pages/                  # Page components
│   │   └── services/               # API services
│   └── package.json
│
├── src/main/
│   ├── java/com/archive/paperlessworld/
│   │   ├── config/                 # Configuration
│   │   ├── controller/             # REST Controllers
│   │   ├── dao/                    # JDBC DAOs
│   │   ├── model/                  # Domain models
│   │   ├── security/               # Security components
│   │   ├── service/                # Business logic
│   │   └── servlet/                # JSP Servlets
│   └── resources/
│       └── application.properties
│
├── pom.xml                         # Maven dependencies
├── schema.sql                      # Database schema
└── README.md                       # This file
```

---

## � UML Diagrams & Design

For comprehensive system design documentation including:
- **Class Diagrams** (Domain Models, DAOs, Exception Hierarchy)
- **Sequence Diagrams** (Login flows, API interactions)
- **Component Diagrams** (Threading Architecture)
- **Architecture Explanations**

👉 **See [DESIGN_DOCUMENT.md](DESIGN_DOCUMENT.md)** for complete UML diagrams and architectural decisions.

### Quick Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│              React Frontend (Port 3000)                  │
│              JSP Pages (Port 8080)                       │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────┴────────────────────────────────────┐
│         Spring Boot Application (Port 8080)              │
│  Controllers → Services → DAOs → JDBC → MySQL           │
└──────────────────────────────────────────────────────────┘
```

---

## �📡 API Documentation

### Authentication

**POST /api/auth/register** - Register new user
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "researcher"
}
```

**POST /api/auth/login** - Login
```json
{
  "email": "admin@paperless.com",
  "password": "admin123"
}
```

### Documents

**GET /api/documents** - Get all documents (with auth header)

**POST /api/documents** - Upload document (Admin only)

### Annotations

**GET /api/annotations/document/{id}** - Get document annotations

**POST /api/annotations** - Add annotation

### Feedback

**GET /api/feedback** - Get all feedback

**POST /api/feedback** - Submit feedback

---

## 🎓 Academic Compliance

### Core Technologies
- ✅ Java 21 with modern features
- ✅ JDBC with PreparedStatement
- ✅ Servlets (3 servlets implemented)
- ✅ JSP with JSTL tags
- ✅ Multi-threading (ExecutorService, @Async)
- ✅ BCrypt password security

### Advanced Features
- ✅ RESTful API design
- ✅ JWT authentication
- ✅ React SPA frontend
- ✅ Spring Boot framework
- ✅ Maven build management
- ✅ MVC architecture

---

## 🐛 Troubleshooting

### Port already in use
Change port in `application.properties`:
```properties
server.port=8081
```

### Database connection failed
Check credentials in `application.properties`:
```properties
spring.datasource.password=your_password
```

### Frontend proxy errors
Verify `client/package.json` has:
```json
"proxy": "http://localhost:8080"
```

### Maven build fails
Ensure Java 21 is installed:
```bash
java -version
```

---

## 📝 License

Educational purposes only.

---

## 👨‍💻 Author

Developed for Java Web Development curriculum demonstrating full-stack development with modern Java technologies.

---

**Happy Coding! Team Double A🚀**
