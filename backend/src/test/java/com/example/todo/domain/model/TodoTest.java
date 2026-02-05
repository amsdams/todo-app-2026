package com.example.todo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

    @Test
    void shouldCreateTodoWithTitleAndDescription() {
        // Given
        String title = "Buy groceries";
        String description = "Milk, eggs, bread";

        // When
        Todo todo = new Todo(title, description);

        // Then
        assertNotNull(todo);
        assertEquals(title, todo.getTitle());
        assertEquals(description, todo.getDescription());
        assertFalse(todo.isCompleted());
        assertNotNull(todo.getCreatedAt());
        assertNotNull(todo.getUpdatedAt());
    }

    @Test
    void shouldMarkTodoAsCompleted() {
        // Given
        Todo todo = new Todo("Test", "Description");
        assertFalse(todo.isCompleted());

        // When
        todo.markAsCompleted();

        // Then
        assertTrue(todo.isCompleted());
    }

    @Test
    void shouldMarkTodoAsIncomplete() {
        // Given
        Todo todo = new Todo("Test", "Description");
        todo.markAsCompleted();
        assertTrue(todo.isCompleted());

        // When
        todo.markAsIncomplete();

        // Then
        assertFalse(todo.isCompleted());
    }

    @Test
    void shouldUpdateTodoDetails() {
        // Given
        Todo todo = new Todo("Original", "Original description");
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        // When
        todo.updateDetails(newTitle, newDescription);

        // Then
        assertEquals(newTitle, todo.getTitle());
        assertEquals(newDescription, todo.getDescription());
    }

    @Test
    void shouldUpdateTimestampWhenMarkedAsCompleted() {
        // Given
        Todo todo = new Todo("Test", "Description");
        var originalUpdatedAt = todo.getUpdatedAt();

        // When
        try {
            Thread.sleep(10); // Small delay to ensure timestamp changes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        todo.markAsCompleted();

        // Then
        assertTrue(todo.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateTimestampWhenDetailsUpdated() {
        // Given
        Todo todo = new Todo("Test", "Description");
        var originalUpdatedAt = todo.getUpdatedAt();

        // When
        try {
            Thread.sleep(10); // Small delay to ensure timestamp changes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        todo.updateDetails("New Title", "New Description");

        // Then
        assertTrue(todo.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}
