package com.example.todo.infrastructure.web.exception;

import com.example.todo.application.service.TodoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private TodoNotFoundException todoNotFoundException;

    @Mock
    private Exception exception;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testHandleTodoNotFoundException() {
        // Arrange
        when(todoNotFoundException.getMessage()).thenReturn("Todo not found");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleTodoNotFoundException(todoNotFoundException);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Not Found", responseBody.get("error"));
        assertEquals("Todo not found", responseBody.get("message"));

        verify(todoNotFoundException).getMessage(); // Verify that getMessage() on TodoNotFoundException was called.
    }

    @Test
    public void testHandleGenericException() {
        // Arrange
        when(exception.getMessage()).thenReturn("Generic error message");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Internal Server Error", responseBody.get("error"));
        assertEquals("Generic error message", responseBody.get("message"));

        verify(exception).getMessage(); // Verify that getMessage() on Exception was called.
    }

}