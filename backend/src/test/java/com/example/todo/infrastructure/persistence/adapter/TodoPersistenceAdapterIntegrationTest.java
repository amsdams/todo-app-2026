package com.example.todo.infrastructure.persistence.adapter;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import com.example.todo.infrastructure.persistence.repository.JpaTodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "com.example.todo.infrastructure.persistence")
@ActiveProfiles("test")
class TodoPersistenceAdapterIntegrationTest {

    @Autowired
    private TodoPersistenceAdapter todoPersistenceAdapter;

    @Autowired
    private JpaTodoRepository jpaTodoRepository;

    @Test
    void shouldSaveTodo() {
        // Given
        Todo todo = new Todo("Test Todo", "Test Description");

        // When
        Todo savedTodo = todoPersistenceAdapter.save(todo);

        // Then
        assertNotNull(savedTodo);
        assertNotNull(savedTodo.getId());
        assertEquals("Test Todo", savedTodo.getTitle());
        assertEquals("Test Description", savedTodo.getDescription());
    }

    @Test
    void shouldFindTodoById() {
        // Given
        TodoEntity entity = TodoEntity.builder()
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        TodoEntity savedEntity = jpaTodoRepository.save(entity);

        // When
        Optional<Todo> result = todoPersistenceAdapter.findById(savedEntity.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Todo", result.get().getTitle());
    }

    @Test
    void shouldFindAllTodos() {
        // Given
        jpaTodoRepository.save(TodoEntity.builder()
                .title("Todo 1")
                .description("Description 1")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());
        
        jpaTodoRepository.save(TodoEntity.builder()
                .title("Todo 2")
                .description("Description 2")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        // When
        List<Todo> todos = todoPersistenceAdapter.findAll();

        // Then
        assertEquals(2, todos.size());
    }

    @Test
    void shouldDeleteTodoById() {
        // Given
        TodoEntity entity = jpaTodoRepository.save(TodoEntity.builder()
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());
        Long id = entity.getId();

        // When
        todoPersistenceAdapter.deleteById(id);

        // Then
        assertFalse(jpaTodoRepository.existsById(id));
    }

    @Test
    void shouldCheckIfTodoExists() {
        // Given
        TodoEntity entity = jpaTodoRepository.save(TodoEntity.builder()
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        // When
        boolean exists = todoPersistenceAdapter.existsById(entity.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldFindCompletedTodos() {
        // Given
        jpaTodoRepository.save(TodoEntity.builder()
                .title("Incomplete Todo")
                .description("Not done")
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());
        
        jpaTodoRepository.save(TodoEntity.builder()
                .title("Completed Todo")
                .description("Done")
                .completed(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        // When
        List<Todo> completedTodos = todoPersistenceAdapter.findCompletedTodos();

        // Then
        assertEquals(1, completedTodos.size());
        assertTrue(completedTodos.getFirst().isCompleted());
        assertEquals("Completed Todo", completedTodos.getFirst().getTitle());
    }

    @Test
    void shouldDeleteMultipleTodos() {
        // Given
        TodoEntity entity1 = jpaTodoRepository.save(TodoEntity.builder()
                .title("Todo 1")
                .description("Description 1")
                .completed(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());
        
        TodoEntity entity2 = jpaTodoRepository.save(TodoEntity.builder()
                .title("Todo 2")
                .description("Description 2")
                .completed(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        Todo todo1 = new Todo("Todo 1", "Description 1");
        todo1.setId(entity1.getId());
        todo1.setCompleted(true);
        
        Todo todo2 = new Todo("Todo 2", "Description 2");
        todo2.setId(entity2.getId());
        todo2.setCompleted(true);

        List<Todo> todosToDelete = List.of(todo1, todo2);

        // When
        todoPersistenceAdapter.deleteAll(todosToDelete);

        // Then
        assertEquals(0, jpaTodoRepository.count());
    }
}
