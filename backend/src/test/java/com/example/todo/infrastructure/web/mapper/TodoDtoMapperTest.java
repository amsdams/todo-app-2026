package com.example.todo.infrastructure.web.mapper;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.TodoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoDtoMapperTest {

    @InjectMocks
    private TodoDtoMapper todoDtoMapper;

    @Mock
    private Todo todo;

    @Test
    public void testToDto() {
        // Arrange
        when(todo.getId()).thenReturn(1L);
        when(todo.getTitle()).thenReturn("Todo title");
        when(todo.getDescription()).thenReturn("Todo description");
        when(todo.isCompleted()).thenReturn(true);

        // Act
        TodoDto result = todoDtoMapper.toDto(todo);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Todo title", result.getTitle());
        assertEquals("Todo description", result.getDescription());
        assertTrue(result.isCompleted());
    }
}