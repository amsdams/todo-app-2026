# Hexagonal Architecture Deep Dive

## What is Hexagonal Architecture?

Hexagonal Architecture (also known as Ports and Adapters) is a software design pattern created by Alistair Cockburn. It aims to create loosely coupled application components that can be easily connected to their software environment through ports and adapters.

## Core Principles

### 1. The Hexagon Represents Your Application Core

The business logic (domain) is at the center and should be:
- **Framework agnostic** - No dependencies on Spring, JPA, etc.
- **Pure business logic** - Only domain concepts and rules
- **Highly testable** - Can be tested without any infrastructure

### 2. Ports

Ports are **interfaces** that define how to interact with the application core.

#### Inbound Ports (Primary/Driving Ports)
- Define **use cases** your application offers
- Called **BY** external actors (UI, API clients)
- Example: Methods in `TodoService` are the inbound port

#### Outbound Ports (Secondary/Driven Ports)
- Define **dependencies** your application needs
- Called **BY** the application core
- Example: `TodoRepository` interface

### 3. Adapters

Adapters are **implementations** that connect ports to the external world.

#### Inbound Adapters (Primary/Driving Adapters)
- **Implement** the application's use cases
- Convert external requests into application calls
- Examples:
  - REST Controller (`TodoController`)
  - GraphQL Resolver
  - CLI Interface
  - Message Queue Consumer

#### Outbound Adapters (Secondary/Driven Adapters)
- **Implement** the outbound ports
- Connect to external systems
- Examples:
  - Database Adapter (`TodoPersistenceAdapter`)
  - Email Service
  - External API Client
  - File System

## Flow of Control

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ HTTP Request
       ↓
┌──────────────────────────────────────────────────┐
│           INBOUND ADAPTER (Primary)              │
│  ┌────────────────────────────────────────┐     │
│  │      TodoController (REST API)         │     │
│  │  - Receives HTTP requests              │     │
│  │  - Maps DTOs to domain objects         │     │
│  │  - Calls application service           │     │
│  └────────────────┬───────────────────────┘     │
└─────────────────────┼────────────────────────────┘
                      │
                      ↓
┌──────────────────────────────────────────────────┐
│          APPLICATION LAYER (Use Cases)           │
│  ┌────────────────────────────────────────┐     │
│  │         TodoService                    │     │
│  │  - Orchestrates business logic         │     │
│  │  - Coordinates domain objects          │     │
│  │  - Uses repository port                │     │
│  └────────────────┬───────────────────────┘     │
└─────────────────────┼────────────────────────────┘
                      │
                      ↓
┌──────────────────────────────────────────────────┐
│              DOMAIN LAYER (Core)                 │
│  ┌────────────────────────────────────────┐     │
│  │         Todo (Domain Model)            │     │
│  │  - Pure business logic                 │     │
│  │  - No framework dependencies           │     │
│  └────────────────────────────────────────┘     │
│  ┌────────────────────────────────────────┐     │
│  │    TodoRepository (Outbound Port)      │     │
│  │  - Interface defining persistence      │     │
│  └────────────────┬───────────────────────┘     │
└─────────────────────┼────────────────────────────┘
                      │
                      ↓
┌──────────────────────────────────────────────────┐
│         OUTBOUND ADAPTER (Secondary)             │
│  ┌────────────────────────────────────────┐     │
│  │   TodoPersistenceAdapter               │     │
│  │  - Implements TodoRepository           │     │
│  │  - Uses JPA for database access        │     │
│  │  - Maps domain ↔ entities              │     │
│  └────────────────┬───────────────────────┘     │
└─────────────────────┼────────────────────────────┘
                      │
                      ↓
                ┌──────────┐
                │ Database │
                └──────────┘
```

## Dependency Rule

**The Dependency Rule**: Dependencies point **INWARD** only.

```
Infrastructure → Application → Domain
     (OUT)           (IN)      (CENTER)
```

- **Domain** depends on nothing
- **Application** depends only on Domain
- **Infrastructure** depends on Application and Domain

This makes the core business logic:
- Independent of frameworks
- Independent of databases
- Independent of UI
- Testable in isolation

## Example Request Flow

Let's trace a request to create a new todo:

### 1. HTTP Request Arrives
```
POST /api/todos
{
  "title": "Buy milk",
  "description": "From the store"
}
```

### 2. Inbound Adapter (TodoController)
```java
@PostMapping
public ResponseEntity<TodoDto> createTodo(@RequestBody CreateTodoRequest request) {
    // Adapter converts DTO to domain concepts
    Todo todo = todoService.createTodo(request.getTitle(), request.getDescription());
    // Adapter converts domain back to DTO
    return ResponseEntity.ok(todoDtoMapper.toDto(todo));
}
```

### 3. Application Service (TodoService)
```java
public Todo createTodo(String title, String description) {
    // Pure business logic
    Todo todo = new Todo(title, description);
    // Uses port interface (doesn't know about JPA)
    return todoRepository.save(todo);
}
```

### 4. Domain Model (Todo)
```java
public Todo(String title, String description) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.description = description;
    this.createdAt = LocalDateTime.now();
    // Business rules are here
}
```

### 5. Outbound Adapter (TodoPersistenceAdapter)
```java
@Override
public Todo save(Todo todo) {
    // Adapter converts domain to entity
    TodoEntity entity = todoMapper.toEntity(todo);
    // Uses JPA
    TodoEntity saved = jpaTodoRepository.save(entity);
    // Converts back to domain
    return todoMapper.toDomain(saved);
}
```

## Benefits Demonstrated

### 1. Testability
Test the domain without any infrastructure:
```java
@Test
void shouldMarkTodoAsCompleted() {
    Todo todo = new Todo("Test", "Description");
    todo.markAsCompleted();
    assertTrue(todo.isCompleted());
    // No database, no Spring, just pure logic
}
```

### 2. Flexibility
Want to switch from JPA to MongoDB?
- Domain layer: **No changes**
- Application layer: **No changes**
- Only create new `MongoTodoPersistenceAdapter`

### 3. Framework Independence
The core logic doesn't know about:
- Spring annotations
- JPA entities
- HTTP requests
- Database specifics

### 4. Clear Boundaries
Each layer has a single responsibility:
- **Domain**: Business rules
- **Application**: Use case orchestration
- **Infrastructure**: Technical details

## Comparison with Traditional Layered Architecture

### Traditional Layers
```
Presentation → Business → Data Access → Database
```
**Problem**: Business layer depends on data layer (JPA entities, repositories)

### Hexagonal Architecture
```
          ┌─────────────┐
          │   Domain    │ ← Independent
          └──────┬──────┘
                 │
    ┌────────────┴────────────┐
    ↓                         ↓
Inbound Adapters      Outbound Adapters
(Controllers)         (Repositories)
```
**Benefit**: Domain is completely independent

## When to Use Hexagonal Architecture?

### Good For:
- ✅ Complex business logic
- ✅ Long-lived applications
- ✅ Applications that need to adapt to changing requirements
- ✅ When testability is crucial
- ✅ Microservices

### Overkill For:
- ❌ Simple CRUD applications
- ❌ Prototypes
- ❌ Scripts and utilities
- ❌ Very small projects

## Key Takeaways

1. **Ports** are interfaces, **Adapters** are implementations
2. **Domain** is the center and depends on nothing
3. **Infrastructure** depends on the domain, never vice versa
4. **Inbound** adapters drive the application (UI, API)
5. **Outbound** adapters are driven by the application (DB, Email)
6. This architecture makes your code more **maintainable**, **testable**, and **flexible**

## Further Reading

- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
