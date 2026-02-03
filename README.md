# Todo App with Hexagonal Architecture

A full-stack todo application built with Spring Boot (backend) and Angular (frontend), implementing hexagonal architecture (also known as ports and adapters architecture).

## Architecture Overview

### Hexagonal Architecture (Ports and Adapters)

This application follows hexagonal architecture principles to achieve clean separation of concerns and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                     SPRING BOOT BACKEND                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              DOMAIN LAYER (Core Business)             │  │
│  │  - Todo.java (Domain Model)                           │  │
│  │  - TodoRepository.java (Outbound Port Interface)      │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↑↓                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           APPLICATION LAYER (Use Cases)               │  │
│  │  - TodoService.java (Business Logic)                  │  │
│  │  - TodoNotFoundException.java                         │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↑↓                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │         INFRASTRUCTURE LAYER (Adapters)               │  │
│  │                                                         │  │
│  │  INBOUND ADAPTERS (Primary/Driving):                  │  │
│  │  - TodoController.java (REST API)                     │  │
│  │  - TodoDto.java                                        │  │
│  │  - TodoDtoMapper.java                                  │  │
│  │  - GlobalExceptionHandler.java                        │  │
│  │                                                         │  │
│  │  OUTBOUND ADAPTERS (Secondary/Driven):                │  │
│  │  - TodoPersistenceAdapter.java (Repository Impl)      │  │
│  │  - JpaTodoRepository.java (Spring Data JPA)           │  │
│  │  - TodoEntity.java (JPA Entity)                        │  │
│  │  - TodoMapper.java (Entity ↔ Domain)                  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↑↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                     ANGULAR FRONTEND                         │
├─────────────────────────────────────────────────────────────┤
│  - AppComponent (UI Component)                               │
│  - TodoService (HTTP Client)                                 │
│  - Todo Model (TypeScript Interface)                         │
└─────────────────────────────────────────────────────────────┘
```

### Key Concepts

#### 1. **Domain Layer** (Inside the Hexagon)
- **Pure business logic** with no framework dependencies
- `Todo.java`: Core domain model with business methods
- `TodoRepository`: Port (interface) for persistence

#### 2. **Application Layer**
- **Use cases** and orchestration
- `TodoService.java`: Implements business workflows
- Uses domain ports to interact with infrastructure

#### 3. **Infrastructure Layer** (Outside the Hexagon)
- **Adapters** that implement the ports

**Inbound Adapters** (Primary/Driving):
- `TodoController.java`: REST API adapter
- Converts HTTP requests to application service calls
- Maps between DTOs and domain models

**Outbound Adapters** (Secondary/Driven):
- `TodoPersistenceAdapter.java`: Implements `TodoRepository` port
- Uses JPA for database operations
- Maps between domain models and JPA entities

### Benefits of This Architecture

1. **Framework Independence**: Core business logic doesn't depend on Spring or JPA
2. **Testability**: Easy to test business logic in isolation
3. **Flexibility**: Easy to swap adapters (e.g., replace JPA with MongoDB)
4. **Clear Boundaries**: Well-defined separation between layers
5. **Maintainability**: Changes in one layer don't affect others

## Project Structure

```
todo-app/
├── backend/                          # Spring Boot Backend
│   ├── pom.xml
│   └── src/main/java/com/example/todo/
│       ├── TodoApplication.java
│       ├── domain/                   # DOMAIN LAYER
│       │   ├── model/
│       │   │   └── Todo.java
│       │   └── port/
│       │       └── TodoRepository.java
│       ├── application/              # APPLICATION LAYER
│       │   └── service/
│       │       ├── TodoService.java
│       │       └── TodoNotFoundException.java
│       └── infrastructure/           # INFRASTRUCTURE LAYER
│           ├── web/                  # Inbound Adapter
│           │   ├── controller/
│           │   │   └── TodoController.java
│           │   ├── dto/
│           │   │   ├── TodoDto.java
│           │   │   └── CreateTodoRequest.java
│           │   ├── mapper/
│           │   │   └── TodoDtoMapper.java
│           │   └── exception/
│           │       └── GlobalExceptionHandler.java
│           └── persistence/          # Outbound Adapter
│               ├── adapter/
│               │   └── TodoPersistenceAdapter.java
│               ├── entity/
│               │   └── TodoEntity.java
│               ├── repository/
│               │   └── JpaTodoRepository.java
│               └── mapper/
│                   └── TodoMapper.java
│
└── frontend/                         # Angular Frontend
    ├── package.json
    ├── angular.json
    ├── tsconfig.json
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css
        └── app/
            ├── app.component.ts
            ├── app.component.html
            ├── app.component.css
            ├── models/
            │   └── todo.model.ts
            └── services/
                └── todo.service.ts
```

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Node.js 18+** and npm
- **Angular CLI** (`npm install -g @angular/cli`)

## Setup and Running

### Backend (Spring Boot)

1. Navigate to backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**Available Endpoints:**
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get todo by ID
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `PATCH /api/todos/{id}/toggle` - Toggle completion status
- `DELETE /api/todos/{id}` - Delete todo

**API Documentation:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api-docs`

See [SWAGGER_DOCUMENTATION.md](SWAGGER_DOCUMENTATION.md) for detailed API documentation guide.

### Frontend (Lit)

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:4200`

**Build for production:**
```bash
npm run build
```

## Features

- ✅ Create new todos with title and description
- ✅ View all todos in a list
- ✅ Mark todos as complete/incomplete
- ✅ Edit existing todos
- ✅ Delete todos
- ✅ Responsive UI design
- ✅ Real-time updates
- ✅ Clean hexagonal architecture
- ✅ Automated batch job to delete completed todos (runs daily at 2 AM)
- ✅ Database sequence for IDs
- ✅ Lombok for reduced boilerplate
- ✅ Comprehensive unit and integration tests
- ✅ Lightweight frontend with Lit (Web Components)

## Database

The application uses **H2 in-memory database** for simplicity. Data is reset when the application restarts.

Access H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:tododb`
- Username: `sa`
- Password: (leave empty)

### Batch Jobs

The application includes a scheduled batch job that runs daily at 2 AM to delete all completed todos.

**Configuration:**
- Schedule: `0 0 2 * * ?` (Every day at 2 AM)
- Location: `TodoBatchScheduler.java`

**To change the schedule:**
```java
@Scheduled(cron = "0 0 * * * ?")  // Every hour
@Scheduled(cron = "0 */30 * * * ?")  // Every 30 minutes
```

**Manual execution:**
The batch job can also be triggered manually through the Spring Batch admin endpoints.

## API Examples

### Create Todo
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy groceries", "description": "Milk, eggs, bread"}'
```

### Get All Todos
```bash
curl http://localhost:8080/api/todos
```

### Toggle Completion
```bash
curl -X PATCH http://localhost:8080/api/todos/1/toggle
```

### Delete Todo
```bash
curl -X DELETE http://localhost:8080/api/todos/1
```

## Testing

### Run All Tests
```bash
cd backend
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=TodoServiceTest
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
The project includes:
- **Unit Tests:** Domain models and services
- **Integration Tests:** Controllers and repositories
- **Test Coverage:** Domain, Application, and Infrastructure layers

**Test Classes:**
- `TodoTest` - Domain model tests
- `TodoServiceTest` - Service layer tests with Mockito
- `TodoControllerTest` - REST API tests with MockMvc
- `TodoPersistenceAdapterIntegrationTest` - Database integration tests

## Technology Stack

### Backend
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Batch (for scheduled jobs)
- H2 Database
- Java 21
- Maven
- Lombok
- Springdoc OpenAPI 2.7.0 (Swagger UI)
- JUnit 5 & Mockito (Testing)

### Frontend
- Lit 3.1 (Web Components)
- Vite 5.0 (Build tool)
- Native JavaScript (ES6+)

## Development

### Adding New Features

To add a new feature following hexagonal architecture:

1. **Start with Domain**: Add business logic to domain models
2. **Define Ports**: Create interfaces in `domain.port` package
3. **Implement Use Cases**: Add logic to `TodoService`
4. **Create Adapters**: Implement ports in infrastructure layer
5. **Update UI**: Modify Angular components and services

### Testing Strategy

- **Unit Tests**: Test domain models and services in isolation
- **Integration Tests**: Test adapters with real dependencies
- **E2E Tests**: Test complete user workflows

## License

MIT License - feel free to use this project for learning and development.

## Contributing

Contributions are welcome! This project is designed as an educational example of hexagonal architecture.
