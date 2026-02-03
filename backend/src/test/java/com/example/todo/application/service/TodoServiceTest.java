package com.example.todo.application.service;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = new Todo("Test Todo", "Test Description");
        testTodo.setId(1L);
    }

    @Test
    void shouldCreateTodo() {
        // Given
        String title = "New Todo";
        String description = "New Description";
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When
        Todo result = todoService.createTodo(title, description);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldGetAllTodos() {
        // Given
        List<Todo> todos = Arrays.asList(testTodo, new Todo("Another", "Todo"));
        when(todoRepository.findAll()).thenReturn(todos);

        // When
        List<Todo> result = todoService.getAllTodos();

        // Then
        assertEquals(2, result.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void shouldGetTodoById() {
        // Given
        Long id = 1L;
        when(todoRepository.findById(id)).thenReturn(Optional.of(testTodo));

        // When
        Todo result = todoService.getTodoById(id);

        // Then
        assertNotNull(result);
        assertEquals(testTodo.getId(), result.getId());
        verify(todoRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenTodoNotFound() {
        // Given
        Long id = 999L;
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> todoService.getTodoById(id));
        verify(todoRepository, times(1)).findById(id);
    }

    @Test
    void shouldUpdateTodo() {
        // Given
        Long id = 1L;
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        when(todoRepository.findById(id)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When
        Todo result = todoService.updateTodo(id, newTitle, newDescription);

        // Then
        assertNotNull(result);
        verify(todoRepository, times(1)).findById(id);
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void shouldToggleTodoFromIncompleteToComplete() {
        // Given
        Long id = 1L;
        testTodo.setCompleted(false);
        when(todoRepository.findById(id)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(testTodo)).thenReturn(testTodo);

        // When
        Todo result = todoService.toggleTodoCompletion(id);

        // Then
        assertTrue(testTodo.isCompleted());
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void shouldToggleTodoFromCompleteToIncomplete() {
        // Given
        Long id = 1L;
        testTodo.setCompleted(true);
        when(todoRepository.findById(id)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(testTodo)).thenReturn(testTodo);

        // When
        Todo result = todoService.toggleTodoCompletion(id);

        // Then
        assertFalse(testTodo.isCompleted());
        verify(todoRepository, times(1)).save(testTodo);
    }

    @Test
    void shouldDeleteTodo() {
        // Given
        Long id = 1L;
        when(todoRepository.existsById(id)).thenReturn(true);

        // When
        todoService.deleteTodo(id);

        // Then
        verify(todoRepository, times(1)).existsById(id);
        verify(todoRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTodo() {
        // Given
        Long id = 999L;
        when(todoRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodo(id));
        verify(todoRepository, times(1)).existsById(id);
        verify(todoRepository, never()).deleteById(id);
    }
}
