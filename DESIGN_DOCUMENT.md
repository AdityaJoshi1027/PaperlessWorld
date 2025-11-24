# 📐 Paperless World - System Design Document

**Version:** 1.0  
**Date:** October 29, 2025  
**Project:** Digital Archive Management System  

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Overview](#system-overview)
3. [Architectural Design](#architectural-design)
4. [UML Diagrams](#uml-diagrams)
5. [Design Patterns](#design-patterns)
6. [Technology Stack Rationale](#technology-stack-rationale)
7. [Database Design](#database-design)
8. [Security Architecture](#security-architecture)
9. [Threading and Concurrency](#threading-and-concurrency)
10. [Academic Compliance](#academic-compliance)

---

## 1. Executive Summary

### 1.1 Project Purpose
Paperless World is a full-stack digital archive management system designed to facilitate document storage, retrieval, annotation, and collaborative research. The system implements role-based access control with three user types: Archivists (administrators), Researchers, and Public users.

### 1.2 Key Objectives
- Provide secure document management with role-based access
- Enable collaborative annotation and feedback on archived documents
- Demonstrate enterprise-grade Java web application development
- Fulfill academic requirements: JDBC, Servlets, JSP, Multi-threading
- Implement modern security practices with JWT and BCrypt

### 1.3 Design Philosophy
The architecture follows **separation of concerns** with clear boundaries between presentation, business logic, and data access layers. We employ **industry-standard patterns** (MVC, DAO, Repository) while maintaining **academic requirements** (pure JDBC, traditional servlets).

---

## 2. System Overview

### 2.1 System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                           │
│  ┌──────────────────────────────┐  ┌──────────────────────────────┐│
│  │    React SPA (Port 3000)     │  │   JSP Pages (Port 8080)      ││
│  │  • TypeScript Components     │  │  • login.jsp                 ││
│  │  • React Router              │  │  • register.jsp              ││
│  │  • Axios HTTP Client         │  │  • dashboard.jsp             ││
│  │  • State Management          │  │  • error.jsp                 ││
│  └──────────────────────────────┘  └──────────────────────────────┘│
└───────────────────┬──────────────────────────┬───────────────────────┘
                    │                          │
        REST API (JSON)              HTTP GET/POST (Form Data)
                    │                          │
┌───────────────────┴──────────────────────────┴───────────────────────┐
│                      APPLICATION LAYER (Port 8080)                    │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │               Spring Boot 3.5.0 Application                     │ │
│  ├─────────────────────────────────────────────────────────────────┤ │
│  │  CONTROLLER LAYER                                               │ │
│  │  ┌──────────────────────┐    ┌──────────────────────────────┐  │ │
│  │  │  REST Controllers    │    │   Servlet Controllers        │  │ │
│  │  │  • AuthController    │    │   • LoginServlet             │  │ │
│  │  │  • DocumentController│    │   • RegisterServlet          │  │ │
│  │  │  • AnnotationCtrlr   │    │   • DashboardServlet         │  │ │
│  │  │  • FeedbackController│    │   • LogoutServlet            │  │ │
│  │  └──────────────────────┘    └──────────────────────────────┘  │ │
│  ├─────────────────────────────────────────────────────────────────┤ │
│  │  SECURITY LAYER                                                 │ │
│  │  • JwtAuthenticationFilter  • BCryptPasswordEncoder            │ │
│  │  • SecurityConfig           • JWT Token Provider               │ │
│  ├─────────────────────────────────────────────────────────────────┤ │
│  │  SERVICE LAYER                                                  │ │
│  │  • AuthService              • AsyncTaskService                 │ │
│  │  • Business Logic           • Thread Pool Management           │ │
│  ├─────────────────────────────────────────────────────────────────┤ │
│  │  DATA ACCESS LAYER (DAO)                                        │ │
│  │  • UserJdbcDAO             • AnnotationJdbcDAO                 │ │
│  │  • DocumentJdbcDAO         • FeedbackJdbcDAO                   │ │
│  │  • Pure JDBC with PreparedStatement                            │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬──────────────────────────────────────┘
                                 │
                         JDBC Connection Pool
                          (HikariCP via Spring)
                                 │
┌────────────────────────────────┴──────────────────────────────────────┐
│                      DATA LAYER (MySQL 8.0+)                          │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │   users     │  │  documents   │  │ annotations  │  │ feedback  │ │
│  │  (7 rows)   │  │  (1+ rows)   │  │  (1+ rows)   │  │ (0 rows)  │ │
│  └─────────────┘  └──────────────┘  └──────────────┘  └───────────┘ │
└───────────────────────────────────────────────────────────────────────┘
```

### 2.2 Request Flow Examples

#### REST API Flow (React → Spring Boot → MySQL)
```
1. User clicks "Login" in React app
2. Axios POST to http://localhost:8080/api/auth/login
3. AuthController receives request
4. AuthService validates credentials
5. UserJdbcDAO queries MySQL via JDBC
6. BCrypt verifies password hash
7. JWT token generated and returned
8. React stores token in localStorage
9. Subsequent requests include Authorization header
```

#### Servlet Flow (Browser → JSP → Spring Boot → MySQL)
```
1. User navigates to http://localhost:8080/servlet/login
2. LoginServlet.doGet() forwards to login.jsp
3. JSP renders login form
4. User submits form (POST request)
5. LoginServlet.doPost() receives parameters
6. UserJdbcDAO validates credentials via JDBC
7. HttpSession created with user attributes
8. Redirect to /servlet/dashboard
9. DashboardServlet checks session
10. dashboard.jsp rendered with user data
```

---

## 3. Architectural Design

### 3.1 Three-Tier Architecture

#### Tier 1: Presentation Layer
- **React SPA**: Modern, responsive UI with TypeScript
- **JSP Pages**: Traditional server-side rendering for academic requirements
- **Design Pattern**: Component-based (React) + MVC (JSP)

#### Tier 2: Application Layer
- **Spring Boot Framework**: Dependency injection, auto-configuration
- **REST Controllers**: Stateless API endpoints (JSON responses)
- **Servlets**: Traditional HttpServlet with session management
- **Services**: Business logic encapsulation
- **DAOs**: Data access abstraction

#### Tier 3: Data Layer
- **MySQL Database**: Relational data storage
- **JDBC**: Direct database connectivity (no ORM)
- **Connection Pooling**: HikariCP for performance
- **Transaction Management**: Manual commit/rollback

### 3.2 Design Decisions

#### Why Spring Boot + Traditional Servlets?
**Decision**: Hybrid approach combining modern framework with traditional Java EE  
**Rationale**:
- Spring Boot provides dependency injection, security, and configuration
- Traditional servlets fulfill academic requirements
- Both can coexist using `@Component` and `@WebServlet` annotations
- Demonstrates understanding of both modern and classic approaches

#### Why JDBC Instead of JPA/Hibernate?
**Decision**: Pure JDBC with PreparedStatement  
**Rationale**:
- Academic requirement explicitly mandates JDBC
- Full control over SQL queries and transactions
- Better understanding of database operations
- Explicit transaction management with commit/rollback
- PreparedStatement prevents SQL injection

#### Why JWT + BCrypt?
**Decision**: Token-based authentication with secure password hashing  
**Rationale**:
- Stateless authentication for RESTful APIs
- BCrypt uses adaptive hashing (computationally expensive)
- Tokens enable mobile/SPA client compatibility
- Industry-standard security practices

---

## 4. UML Diagrams

### 4.1 Class Diagram - Core Domain Models

```
┌─────────────────────────────────────────────────────────────────────┐
│                           Domain Models                              │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────┐
│       «POJO»             │
│         User             │
├──────────────────────────┤
│ - id: String             │
│ - name: String           │
│ - email: String          │
│ - password: String       │
│ - role: String           │
│ - status: String         │
│ - accessLevel: String    │
│ - createdAt: LocalDateTime│
│ - updatedAt: LocalDateTime│
├──────────────────────────┤
│ + getId(): String        │
│ + setId(String): void    │
│ + getName(): String      │
│ + setName(String): void  │
│ + getEmail(): String     │
│ + ...                    │
└──────────────────────────┘
            │
            │ 1
            │
            │ uploaded_by
            ▼ *
┌──────────────────────────┐
│       «POJO»             │
│    ArchiveDocument       │
├──────────────────────────┤
│ - id: String             │
│ - title: String          │
│ - description: String    │
│ - fileName: String       │
│ - filePath: String       │
│ - fileType: String       │
│ - fileSize: Long         │
│ - uploaderId: String     │◄───┐
│ - category: String       │    │
│ - tags: String           │    │
│ - accessLevel: String    │    │ 1
│ - createdAt: LocalDateTime│   │
│ - updatedAt: LocalDateTime│   │
├──────────────────────────┤    │
│ + getId(): String        │    │
│ + setId(String): void    │    │
│ + ...                    │    │
└──────────────────────────┘    │
            │                   │
            │ 1                 │
            │                   │
            │ document_id       │
            ▼ *                 │
┌──────────────────────────┐    │
│       «POJO»             │    │
│      Annotation          │    │
├──────────────────────────┤    │
│ - id: String             │    │
│ - documentId: String     │    │
│ - userId: String         │────┘
│ - content: String        │    user_id
│ - pageNumber: Integer    │
│ - positionX: Double      │
│ - positionY: Double      │
│ - annotationType: String │
│ - createdAt: LocalDateTime│
│ - updatedAt: LocalDateTime│
├──────────────────────────┤
│ + getId(): String        │
│ + setId(String): void    │
│ + ...                    │
└──────────────────────────┘

            ┌──────────────────────────┐
            │       «POJO»             │
            │       Feedback           │
            ├──────────────────────────┤
            │ - id: String             │
            │ - userId: String         │─────┐
            │ - documentId: String     │     │ user_id
            │ - subject: String        │     │
            │ - message: String        │     │
            │ - status: String         │     ▼ 1
            │ - priority: String       │   User
            │ - createdAt: LocalDateTime│
            │ - updatedAt: LocalDateTime│
            ├──────────────────────────┤
            │ + getId(): String        │
            │ + setId(String): void    │
            │ + ...                    │
            └──────────────────────────┘
```

### 4.2 Class Diagram - Data Access Layer

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Data Access Objects (DAO)                       │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────┐
│    «Repository»          │
│     UserJdbcDAO          │
├──────────────────────────┤
│ - dataSource: DataSource │
├──────────────────────────┤
│ + findById(String): Optional<User>      │
│ + findByEmail(String): Optional<User>   │
│ + findAll(): List<User>                 │
│ + findByRole(String): List<User>        │
│ + save(User): User                      │
│ + deleteById(String): void              │
│ + updateStatus(String, String): void    │
│ + count(): long                         │
│ - mapResultSetToUser(ResultSet): User   │
└──────────────────────────┘
            │
            │ uses
            ▼
┌──────────────────────────┐
│     «DataSource»         │
│      HikariCP            │
├──────────────────────────┤
│ + getConnection()        │
└──────────────────────────┘
            │
            │ JDBC
            ▼
┌──────────────────────────┐
│      «Database»          │
│     MySQL 8.0+           │
├──────────────────────────┤
│ • users table            │
│ • documents table        │
│ • annotations table      │
│ • feedback table         │
└──────────────────────────┘

┌──────────────────────────┐
│    «Repository»          │
│   DocumentJdbcDAO        │
├──────────────────────────┤
│ - dataSource: DataSource │
├──────────────────────────┤
│ + findById(String): Optional<ArchiveDocument>│
│ + findAll(): List<ArchiveDocument>           │
│ + findByUploaderId(String): List<...>        │
│ + findByCategory(String): List<...>          │
│ + save(ArchiveDocument): ArchiveDocument     │
│ + deleteById(String): void                   │
│ + updateAccessLevel(String, String): void    │
│ - mapResultSetToDocument(ResultSet): ...     │
└──────────────────────────┘

┌──────────────────────────┐
│    «Repository»          │
│  AnnotationJdbcDAO       │
├──────────────────────────┤
│ - dataSource: DataSource │
├──────────────────────────┤
│ + findById(String): Optional<Annotation>     │
│ + findByDocumentId(String): List<Annotation> │
│ + findByUserId(String): List<Annotation>     │
│ + save(Annotation): Annotation               │
│ + deleteById(String): void                   │
│ - mapResultSetToAnnotation(ResultSet): ...   │
└──────────────────────────┘

┌──────────────────────────┐
│    «Repository»          │
│   FeedbackJdbcDAO        │
├──────────────────────────┤
│ - dataSource: DataSource │
├──────────────────────────┤
│ + findById(String): Optional<Feedback>       │
│ + findAll(): List<Feedback>                  │
│ + findByUserId(String): List<Feedback>       │
│ + findByStatus(String): List<Feedback>       │
│ + save(Feedback): Feedback                   │
│ + deleteById(String): void                   │
│ + updateStatus(String, String): void         │
│ - mapResultSetToFeedback(ResultSet): ...     │
└──────────────────────────┘
```

### 4.3 Sequence Diagram - User Login (REST API)

```
┌────────┐         ┌──────────────┐      ┌──────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐
│ React  │         │AuthController│      │AuthService│     │UserDAO  │      │ MySQL   │      │ JWT     │
│  App   │         │              │      │          │      │         │      │Database │      │Provider │
└───┬────┘         └──────┬───────┘      └────┬─────┘      └────┬────┘      └────┬────┘      └────┬────┘
    │                     │                   │                  │                │                │
    │ POST /api/auth/login│                   │                  │                │                │
    │ {email, password}   │                   │                  │                │                │
    ├────────────────────►│                   │                  │                │                │
    │                     │                   │                  │                │                │
    │                     │ login(email, pwd) │                  │                │                │
    │                     ├──────────────────►│                  │                │                │
    │                     │                   │                  │                │                │
    │                     │                   │ findByEmail(email)                │                │
    │                     │                   ├─────────────────►│                │                │
    │                     │                   │                  │                │                │
    │                     │                   │                  │ SELECT * FROM users            │
    │                     │                   │                  │ WHERE email = ?│                │
    │                     │                   │                  ├───────────────►│                │
    │                     │                   │                  │                │                │
    │                     │                   │                  │ ResultSet      │                │
    │                     │                   │                  │◄───────────────┤                │
    │                     │                   │                  │                │                │
    │                     │                   │ Optional<User>   │                │                │
    │                     │                   │◄─────────────────┤                │                │
    │                     │                   │                  │                │                │
    │                     │                   │ [User found]     │                │                │
    │                     │                   │                  │                │                │
    │                     │                   │ passwordEncoder.matches(pwd, hash)│                │
    │                     │                   │───────────────────────────────────┤                │
    │                     │                   │                  │                │                │
    │                     │                   │ [Password valid] │                │                │
    │                     │                   │                  │                │                │
    │                     │                   │                  │                │ generateToken()│
    │                     │                   │                  │                │───────────────►│
    │                     │                   │                  │                │                │
    │                     │                   │                  │                │ JWT Token      │
    │                     │                   │                  │                │◄───────────────┤
    │                     │                   │                  │                │                │
    │                     │ LoginResponse     │                  │                │                │
    │                     │ (token, user)     │                  │                │                │
    │                     │◄──────────────────┤                  │                │                │
    │                     │                   │                  │                │                │
    │ HTTP 200 OK         │                   │                  │                │                │
    │ {token, user}       │                   │                  │                │                │
    │◄────────────────────┤                   │                  │                │                │
    │                     │                   │                  │                │                │
    │ Store token in      │                   │                  │                │                │
    │ localStorage        │                   │                  │                │                │
    │─────────────────┐   │                   │                  │                │                │
    │                 │   │                   │                  │                │                │
    │◄────────────────┘   │                   │                  │                │                │
    │                     │                   │                  │                │                │
```

### 4.4 Sequence Diagram - User Login (Servlet/JSP)

```
┌─────────┐      ┌──────────────┐      ┌─────────┐      ┌─────────┐      ┌─────────┐
│ Browser │      │LoginServlet  │      │UserDAO  │      │ MySQL   │      │HttpSession│
│         │      │              │      │         │      │Database │      │         │
└────┬────┘      └──────┬───────┘      └────┬────┘      └────┬────┘      └────┬────┘
     │                  │                   │                │                │
     │ GET /servlet/login                   │                │                │
     ├─────────────────►│                   │                │                │
     │                  │                   │                │                │
     │                  │ doGet()           │                │                │
     │                  │────────┐          │                │                │
     │                  │        │          │                │                │
     │                  │◄───────┘          │                │                │
     │                  │                   │                │                │
     │                  │ forward to login.jsp               │                │
     │                  │───────────────────────────────────►│                │
     │                  │                                    │                │
     │ HTML Form        │                                    │                │
     │◄─────────────────┤                                    │                │
     │                  │                                    │                │
     │ POST /servlet/login                                   │                │
     │ {email, password}│                                    │                │
     ├─────────────────►│                                    │                │
     │                  │                                    │                │
     │                  │ doPost()                           │                │
     │                  │────────┐                           │                │
     │                  │        │                           │                │
     │                  │◄───────┘                           │                │
     │                  │                                    │                │
     │                  │ findByEmail(email)                 │                │
     │                  ├──────────────────►│                │                │
     │                  │                   │                │                │
     │                  │                   │ JDBC Query     │                │
     │                  │                   ├───────────────►│                │
     │                  │                   │                │                │
     │                  │                   │ User data      │                │
     │                  │                   │◄───────────────┤                │
     │                  │                   │                │                │
     │                  │ Optional<User>    │                │                │
     │                  │◄──────────────────┤                │                │
     │                  │                   │                │                │
     │                  │ [Verify password] │                │                │
     │                  │────────┐          │                │                │
     │                  │        │          │                │                │
     │                  │◄───────┘          │                │                │
     │                  │                   │                │                │
     │                  │                   │                │ getSession(true)│
     │                  │                   │                │───────────────►│
     │                  │                   │                │                │
     │                  │                   │                │ setAttribute() │
     │                  │                   │                │───────────────►│
     │                  │                   │                │ (user, userId, │
     │                  │                   │                │  role, etc.)   │
     │                  │                   │                │                │
     │                  │ sendRedirect("/servlet/dashboard") │                │
     │                  │───────────────────────────────────────────────────►│
     │                  │                                    │                │
     │ HTTP 302 Redirect│                                    │                │
     │◄─────────────────┤                                    │                │
     │                  │                                    │                │
     │ GET /servlet/dashboard                                │                │
     ├─────────────────────────────────────────────────────────────────────►│
     │                  │                                    │                │
```

### 4.5 Class Diagram - Exception Hierarchy

```
┌──────────────────────────────────────────────────────────────┐
│                    Exception Hierarchy                        │
└──────────────────────────────────────────────────────────────┘

                    ┌─────────────────┐
                    │ RuntimeException│
                    │  (Java Built-in)│
                    └────────┬────────┘
                             │
                             │ extends
              ┌──────────────┼──────────────┬────────────────┐
              │              │              │                │
              ▼              ▼              ▼                ▼
┌──────────────────────┐ ┌───────────────────────┐ ┌──────────────────────┐
│«Custom Exception»    │ │«Custom Exception»     │ │«Custom Exception»    │
│UserNotFoundException │ │DocumentAccessDenied   │ │DatabaseOperation     │
│                      │ │     Exception         │ │    Exception         │
├──────────────────────┤ ├───────────────────────┤ ├──────────────────────┤
│ + message: String    │ │ - documentId: String  │ │ - operation: String  │
│                      │ │ - userId: String      │ │ - entityType: String │
├──────────────────────┤ │ - requiredAccessLevel │ ├──────────────────────┤
│ + UserNotFoundException│ │   : String          │ │ + DatabaseOperation  │
│   (String)           │ ├───────────────────────┤ │   Exception(String)  │
│ + UserNotFoundException│ │ + DocumentAccessDenied│ │ + DatabaseOperation │
│   (String, Throwable)│ │   Exception(String)   │ │   Exception(String,  │
│ + withId(String):    │ │ + DocumentAccessDenied│ │     Throwable)       │
│   UserNotFound...    │ │   Exception(String,   │ │ + insertFailed(...): │
│ + withEmail(String): │ │     Throwable)        │ │   DatabaseOp...      │
│   UserNotFound...    │ │ + insufficientAccess  │ │ + updateFailed(...): │
└──────────────────────┘ │   Level(...): ...     │ │   DatabaseOp...      │
                         │ + userSuspended(String)│ │ + deleteFailed(...): │
                         │   : DocumentAccess... │ │   DatabaseOp...      │
                         │ + getDocumentId()     │ │ + selectFailed(...): │
                         │ + getUserId()         │ │   DatabaseOp...      │
                         │ + getRequiredAccessLevel│ │ + getOperation()   │
                         └───────────────────────┘ │ + getEntityType()    │
                                                    └──────────────────────┘
                                    │
                                    │ handled by
                                    ▼
                ┌─────────────────────────────────────┐
                │  @RestControllerAdvice             │
                │  GlobalExceptionHandler            │
                ├─────────────────────────────────────┤
                │ + handleUserNotFoundException(...)  │
                │   : ResponseEntity<Map>            │
                │ + handleDocumentAccessDenied(...)  │
                │   : ResponseEntity<Map>            │
                │ + handleDatabaseOperation(...)     │
                │   : ResponseEntity<Map>            │
                │ + handleRuntimeException(...)      │
                │   : ResponseEntity<Map>            │
                └─────────────────────────────────────┘
```

### 4.6 Component Diagram - Threading Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                    Threading & Concurrency                          │
└────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                 AsyncTaskService (@Service)                       │
├──────────────────────────────────────────────────────────────────┤
│  Thread-Safe Collections:                                        │
│  • AtomicInteger taskCounter                                     │
│  • ConcurrentHashMap<String, String> taskResults                 │
│  • CopyOnWriteArrayList<String> notifications                    │
│  • ReadWriteLock cacheLock                                       │
│  • ConcurrentHashMap<String, Object> documentCache               │
└──────────────────┬───────────────────────────────────────────────┘
                   │
                   │ uses
                   ▼
┌──────────────────────────────────────────────────────────────────┐
│              ThreadPoolTaskExecutor Configuration                 │
│                   (ThreadPoolConfig.java)                         │
├──────────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  @Bean("taskExecutor")                                     │  │
│  │  • corePoolSize = 5                                        │  │
│  │  • maxPoolSize = 10                                        │  │
│  │  • queueCapacity = 25                                      │  │
│  │  • threadNamePrefix = "async-task-"                        │  │
│  └────────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  @Bean("documentProcessingExecutor")                       │  │
│  │  • corePoolSize = 3                                        │  │
│  │  • maxPoolSize = 6                                         │  │
│  │  • queueCapacity = 50                                      │  │
│  │  • threadNamePrefix = "doc-processor-"                     │  │
│  └────────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  @Bean("notificationExecutor")                             │  │
│  │  • corePoolSize = 2                                        │  │
│  │  • maxPoolSize = 4                                         │  │
│  │  • queueCapacity = 100                                     │  │
│  │  • threadNamePrefix = "notification-"                      │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
                   │
                   │ manages
                   ▼
┌──────────────────────────────────────────────────────────────────┐
│                    @Async Methods                                 │
├──────────────────────────────────────────────────────────────────┤
│  @Async("taskExecutor")                                          │
│  CompletableFuture<String> sendEmailAsync(...)                   │
│  • Thread.sleep(2000) - simulates email sending                  │
│  • Returns CompletableFuture                                     │
├──────────────────────────────────────────────────────────────────┤
│  @Async("documentProcessingExecutor")                            │
│  CompletableFuture<String> processDocumentAsync(...)             │
│  • Thread.sleep(3000) - simulates heavy processing               │
│  • Uses ReadWriteLock for cache updates                          │
├──────────────────────────────────────────────────────────────────┤
│  @Async("notificationExecutor")                                  │
│  CompletableFuture<Void> sendNotificationAsync(...)              │
│  • CopyOnWriteArrayList for thread-safe notification storage     │
│  • Returns CompletableFuture<Void>                               │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                  Concurrency Utilities Used                       │
├──────────────────────────────────────────────────────────────────┤
│  • AtomicInteger: Lock-free thread-safe counter                  │
│  • ConcurrentHashMap: Thread-safe key-value storage              │
│  • CopyOnWriteArrayList: Thread-safe list for read-heavy ops     │
│  • ReadWriteLock: Multiple readers, exclusive writer             │
│  • CompletableFuture: Async computation result                   │
│  • @Async annotation: Spring's async method execution            │
└──────────────────────────────────────────────────────────────────┘
```

---

## 5. Design Patterns

### 5.1 Patterns Implemented

#### 5.1.1 Model-View-Controller (MVC)
- **Model**: User, ArchiveDocument, Annotation, Feedback POJOs
- **View**: JSP pages, React components
- **Controller**: REST Controllers, Servlets

#### 5.1.2 Data Access Object (DAO)
- Abstracts database operations
- Each entity has dedicated DAO (UserJdbcDAO, DocumentJdbcDAO, etc.)
- Benefits: Separation of concerns, testability, maintainability

#### 5.1.3 Repository Pattern
- Similar to DAO but with Spring stereotype annotation
- Provides higher-level abstraction over data access
- Enables Spring's exception translation

#### 5.1.4 Singleton Pattern
- Spring beans are singleton by default
- DAOs, Services, Controllers are all singletons
- Thread-safe through Spring's management

#### 5.1.5 Factory Pattern
- Custom exception factory methods:
  - `UserNotFoundException.withId(id)`
  - `DocumentAccessDeniedException.insufficientAccessLevel(...)`
  - `DatabaseOperationException.insertFailed(...)`

#### 5.1.6 Template Method Pattern
- HttpServlet's `doGet()` and `doPost()` methods
- Spring's `JdbcTemplate` pattern (though we use raw JDBC)

---

## 6. Technology Stack Rationale

### 6.1 Backend Technologies

#### Java 21
- **Why**: Latest LTS version with modern features
- **Benefits**: Records, pattern matching, virtual threads support
- **Academic Fit**: Demonstrates current Java knowledge

#### Spring Boot 3.5.0
- **Why**: Industry-standard framework with extensive ecosystem
- **Benefits**: Dependency injection, auto-configuration, security
- **Trade-off**: Adds complexity but improves maintainability

#### Pure JDBC (No ORM)
- **Why**: Academic requirement, full SQL control
- **Benefits**: Understanding of database operations, performance tuning
- **Implementation**: PreparedStatement, explicit transactions

#### MySQL 8.0+
- **Why**: Popular open-source RDBMS
- **Benefits**: ACID compliance, good performance, wide adoption
- **Features Used**: Foreign keys, indexes, fulltext search

### 6.2 Frontend Technologies

#### React 18.2.0
- **Why**: Modern SPA framework for responsive UI
- **Benefits**: Component reusability, virtual DOM, large ecosystem
- **Integration**: Communicates via REST API

#### TypeScript
- **Why**: Type safety for JavaScript
- **Benefits**: Catch errors at compile-time, better IDE support
- **Academic Value**: Demonstrates modern web development

### 6.3 Security Technologies

#### JWT (JSON Web Tokens)
- **Why**: Stateless authentication for REST APIs
- **Benefits**: Scalability, mobile-friendly, distributed systems
- **Implementation**: Token generation on login, validation on requests

#### BCrypt
- **Why**: Adaptive password hashing algorithm
- **Benefits**: Computationally expensive (protects against brute-force)
- **Configuration**: Strength 10 (2^10 iterations)

---

## 7. Database Design

### 7.1 Entity-Relationship Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                     Database Schema (MySQL)                       │
└──────────────────────────────────────────────────────────────────┘

┌─────────────────────────┐
│        users            │
├─────────────────────────┤
│ PK  id (INT)            │
│     name (VARCHAR)      │
│ UNQ email (VARCHAR)     │
│     password (VARCHAR)  │
│     role (VARCHAR)      │
│     status (VARCHAR)    │
│     access_level (VARCHAR)│
│     created_at (TIMESTAMP)│
│     updated_at (TIMESTAMP)│
└─────────┬───────────────┘
          │ 1
          │ uploader_id
          │
          │ *
┌─────────▼───────────────┐
│      documents          │
├─────────────────────────┤
│ PK  id (INT)            │
│     title (VARCHAR)     │
│     description (TEXT)  │
│     file_name (VARCHAR) │
│     file_path (VARCHAR) │
│     file_type (VARCHAR) │
│     file_size (BIGINT)  │
│ FK  uploader_id (INT)   │────────┐
│     category (VARCHAR)  │        │
│     tags (VARCHAR)      │        │
│     access_level (VARCHAR)│      │ CASCADE
│     created_at (TIMESTAMP)│      │ ON DELETE
│     updated_at (TIMESTAMP)│      │
└─────────┬───────────────┘        │
          │ 1                      │
          │ document_id            │
          │                        │
          │ *                      │
┌─────────▼───────────────┐        │
│     annotations         │        │
├─────────────────────────┤        │
│ PK  id (INT)            │        │
│ FK  document_id (INT)   │────────┤
│ FK  user_id (INT)       │────────┤
│     content (TEXT)      │        │
│     page_number (INT)   │        │
│     position_x (DOUBLE) │        │
│     position_y (DOUBLE) │        │
│     annotation_type (VARCHAR)│   │
│     created_at (TIMESTAMP)│      │
│     updated_at (TIMESTAMP)│      │
└─────────────────────────┘        │
                                   │
┌─────────────────────────┐        │
│       feedback          │        │
├─────────────────────────┤        │
│ PK  id (INT)            │        │
│ FK  user_id (INT)       │────────┤
│ FK  document_id (INT)   │────────┘ SET NULL
│     subject (VARCHAR)   │          ON DELETE
│     message (TEXT)      │
│     status (VARCHAR)    │
│     priority (VARCHAR)  │
│     created_at (TIMESTAMP)│
│     updated_at (TIMESTAMP)│
└─────────────────────────┘

Indexes:
• users: idx_email, idx_role, idx_status
• documents: idx_uploader, idx_category, idx_access_level, idx_fulltext
• annotations: idx_document, idx_user
• feedback: idx_user, idx_document, idx_status
```

### 7.2 Normalization

**Third Normal Form (3NF)**:
- ✅ No repeating groups (1NF)
- ✅ All non-key attributes depend on primary key (2NF)
- ✅ No transitive dependencies (3NF)

**Denormalization Decisions**:
- `tags` stored as comma-separated string (acceptable for read-heavy operations)
- Could be normalized to separate `tags` table if complex tag queries needed

### 7.3 Transaction Management

```java
// Example from UserJdbcDAO.save()
Connection conn = dataSource.getConnection();
conn.setAutoCommit(false);  // START TRANSACTION

try {
    // Execute SQL operations
    pstmt.executeUpdate();
    
    conn.commit();  // COMMIT
    
} catch (SQLException e) {
    conn.rollback();  // ROLLBACK on error
    throw new DatabaseOperationException(...);
}
```

**ACID Properties Ensured**:
- **Atomicity**: All-or-nothing via commit/rollback
- **Consistency**: Foreign key constraints, data validation
- **Isolation**: MySQL's default REPEATABLE READ level
- **Durability**: InnoDB storage engine with transaction logs

---

## 8. Security Architecture

### 8.1 Authentication Flow

```
1. User submits credentials (email + password)
2. AuthService finds user by email via JDBC
3. BCryptPasswordEncoder.matches() verifies password
4. JwtTokenProvider.generateToken() creates JWT
5. Token returned to client
6. Client stores token (localStorage for React, session for JSP)
7. Subsequent requests include token in Authorization header
8. JwtAuthenticationFilter validates token
9. SecurityContext populated with user details
10. Request proceeds to controller
```

### 8.2 Password Security

**BCrypt Configuration**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);  // 2^10 iterations
}
```

**Why BCrypt?**:
- Adaptive: Cost factor can be increased as hardware improves
- Salted: Each password gets unique random salt
- Slow: Intentionally computationally expensive (prevents brute-force)

**Example Hash**:
```
Plain: admin123
Hash: $2a$10$b.dZT85qaJi3FXs11UesvuFy.D4De4I77YTK2Ebajj8J4oOnCwVxW
      │  │  │                                                      
      │  │  └─ Salt (22 characters)
      │  └─ Cost factor (10 = 2^10 = 1024 iterations)
      └─ BCrypt version (2a)
```

### 8.3 Authorization

**Role-Based Access Control (RBAC)**:
- **archivist**: Full access (admin operations)
- **researcher**: Restricted access (view, annotate)
- **public**: Limited access (view public documents)

**Implementation**:
```java
@PreAuthorize("hasRole('ARCHIVIST')")
public List<User> getAllUsers() { ... }

// In servlets:
String role = (String) session.getAttribute("userRole");
if ("archivist".equals(role)) {
    // Allow admin operations
}
```

---

## 9. Threading and Concurrency

### 9.1 Thread Pool Configuration

**Three Separate Executors**:

1. **Task Executor** (General purpose)
   - Core: 5 threads
   - Max: 10 threads
   - Queue: 25 tasks
   - Use: Email sending, general async tasks

2. **Document Processing Executor**
   - Core: 3 threads
   - Max: 6 threads
   - Queue: 50 tasks
   - Use: Heavy document processing, OCR, indexing

3. **Notification Executor**
   - Core: 2 threads
   - Max: 4 threads
   - Queue: 100 tasks
   - Use: User notifications, alerts

### 9.2 Thread-Safe Collections

```java
// Atomic operations (lock-free)
private final AtomicInteger taskCounter = new AtomicInteger(0);
int count = taskCounter.incrementAndGet();

// Concurrent hash map (thread-safe)
private final ConcurrentHashMap<String, String> taskResults = new ConcurrentHashMap<>();
taskResults.put("key", "value");

// Copy-on-write list (read-optimized)
private final CopyOnWriteArrayList<String> notifications = new CopyOnWriteArrayList<>();
notifications.add("notification");

// Read-write lock (multiple readers, exclusive writer)
private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
cacheLock.readLock().lock();   // Multiple readers allowed
cacheLock.writeLock().lock();  // Exclusive write access
```

### 9.3 Async Method Execution

```java
@Async("taskExecutor")
public CompletableFuture<String> sendEmailAsync(String recipient, String subject) {
    // Executes in separate thread from pool
    Thread.sleep(2000);  // Simulate work
    return CompletableFuture.completedFuture("Email sent");
}

// Usage:
CompletableFuture<String> future = asyncService.sendEmailAsync(...);
String result = future.get();  // Blocking wait
// Or use callbacks: future.thenAccept(result -> ...)
```

---

## 10. Academic Compliance

### 10.1 Requirements Checklist

✅ **JDBC Integration**
- Pure JDBC with PreparedStatement
- No ORM (Hibernate/JPA)
- Explicit transaction management
- Connection pooling (HikariCP)
- 4 DAO classes with full CRUD operations

✅ **Servlets**
- 4 servlets extending HttpServlet
- @WebServlet annotations
- doGet() and doPost() implementations
- Request parameter handling
- Response redirects and forwards

✅ **JSP Integration**
- 4 JSP pages with JSTL tags
- RequestDispatcher forwarding
- EL expressions (${...})
- Form submission to servlets

✅ **Session Management**
- HttpSession creation and management
- setAttribute() / getAttribute()
- Session timeout configuration
- Session invalidation on logout

✅ **Multi-threading**
- 3 ThreadPoolTaskExecutor configurations
- @Async method execution
- Thread-safe collections (Concurrent*, Atomic*, CopyOnWrite*)
- ReadWriteLock for synchronization
- CompletableFuture for async results

✅ **Exception Handling**
- 3 custom exception classes
- Global exception handler (@RestControllerAdvice)
- Try-catch blocks in all DAOs
- Proper exception propagation

✅ **OOP Concepts**
- Classes and objects (4 domain models)
- Inheritance (servlets extend HttpServlet)
- Encapsulation (private fields, getters/setters)
- Polymorphism (DAO implementations)

✅ **Collections Framework**
- ArrayList, HashMap (standard collections)
- ConcurrentHashMap (thread-safe)
- CopyOnWriteArrayList (thread-safe)
- Generic types throughout

### 10.2 Academic Value Additions

**Beyond Requirements**:
1. **Design Documentation**: This comprehensive document with UML diagrams
2. **Custom Exceptions**: Domain-specific error handling
3. **Security**: Industry-standard JWT + BCrypt
4. **Modern Framework**: Spring Boot integration
5. **RESTful API**: Professional API design
6. **React Frontend**: Modern SPA architecture
7. **Code Quality**: Javadoc comments, clean code principles

---

## 11. Conclusion

### 11.1 Design Achievements

This system successfully demonstrates:
- **Enterprise-grade architecture** with clear separation of concerns
- **Academic compliance** with all required technologies (JDBC, Servlets, JSP, Threading)
- **Modern practices** (REST APIs, JWT, React, TypeScript)
- **Security best practices** (BCrypt, token authentication, role-based access)
- **Scalability** (thread pools, connection pooling, async processing)

### 11.2 Learning Outcomes

Students/developers working on this project gain experience with:
- Full-stack Java web development
- Database design and SQL optimization
- Multi-threading and concurrency
- Security implementation
- Modern vs traditional Java EE approaches
- System design and architecture documentation

### 11.3 Future Enhancements

Potential improvements:
1. Implement actual file upload/storage (AWS S3, local filesystem)
2. Add full-text search with Elasticsearch
3. Implement WebSocket for real-time notifications
4. Add caching layer (Redis)
5. Implement audit logging
6. Add comprehensive unit and integration tests
7. Implement CI/CD pipeline

---

**Document Version**: 1.0  
**Last Updated**: October 29, 2025  
**Author**: Paperless World Development Team  
**Status**: Complete and Ready for Submission
