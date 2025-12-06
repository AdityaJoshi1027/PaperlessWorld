# 📐 System Design Document

## 1. System Architecture

The Paperless World application follows a standard MVC (Model-View-Controller) architecture integrated with Spring Boot's layered architecture.

```mermaid
graph TD
    Client[Client Layer\n(React / JSP)] -->|HTTP/REST| Controller[Controller Layer\n(Spring MVC / Servlets)]
    Controller -->|Calls| Service[Service Layer\n(Business Logic)]
    Service -->|Calls| DAO[DAO Layer\n(Data Access)]
    DAO -->|JDBC| DB[(MySQL Database)]
    
    subgraph Backend
    Controller
    Service
    DAO
    end
```

## 2. Database Schema (ER Diagram)

The database consists of four main entities with the following relationships:

```mermaid
erDiagram
    USERS ||--o{ DOCUMENTS : uploads
    USERS ||--o{ ANNOTATIONS : creates
    USERS ||--o{ FEEDBACK : submits
    DOCUMENTS ||--o{ ANNOTATIONS : has
    DOCUMENTS ||--o{ FEEDBACK : receives

    USERS {
        bigint id PK
        string email
        string password
        string role
        string name
    }

    DOCUMENTS {
        bigint id PK
        string title
        string filepath
        bigint uploader_id FK
        timestamp created_at
    }

    ANNOTATIONS {
        bigint id PK
        string content
        bigint document_id FK
        bigint user_id FK
        int page_number
    }

    FEEDBACK {
        bigint id PK
        string message
        bigint document_id FK
        bigint user_id FK
        int rating
    }
```

## 3. Class Diagram (Core Components)

Key classes demonstrating the relationship between Controllers, Services, and DAOs.

```mermaid
classDiagram
    class AuthController {
        +login(LoginRequest)
        +register(RegisterRequest)
    }
    
    class AuthService {
        +authenticate(String, String)
        +registerUser(User)
    }
    
    class UserJdbcDAO {
        +findById(String)
        +findByEmail(String)
        +save(User)
    }
    
    class User {
        -Long id
        -String email
        -String role
    }

    AuthController --> AuthService
    AuthService --> UserJdbcDAO
    UserJdbcDAO ..> User
```

## 4. Sequence Diagram: User Login

The flow of a login request through the system.

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant UserJdbcDAO
    participant Database

    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: authenticate(email, password)
    AuthService->>UserJdbcDAO: findByEmail(email)
    UserJdbcDAO->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserJdbcDAO: ResultSet
    UserJdbcDAO-->>AuthService: User object
    AuthService->>AuthService: verifyPassword()
    AuthService-->>AuthController: AuthResponse (JWT)
    AuthController-->>Client: 200 OK + Token
```

## 5. Threading Model

The application uses `CompletableFuture` for asynchronous operations to improve performance.

```mermaid
graph LR
    Request[Incoming Request] --> MainThread[Servlet Thread]
    MainThread -->|Offload| AsyncPool[Async Task Executor]
    AsyncPool -->|Process| Worker1[Worker Thread 1]
    AsyncPool -->|Process| Worker2[Worker Thread 2]
    Worker1 -->|Result| Future[CompletableFuture]
    Future -->|Join| Response[Send Response]
```
