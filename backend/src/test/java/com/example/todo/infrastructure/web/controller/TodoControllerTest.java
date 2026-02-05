package com.example.todo.infrastructure.web.controller;

import com.example.todo.application.service.TodoService;
import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.CreateTodoRequest;
import com.example.todo.infrastructure.web.mapper.TodoDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = TodoController.class
)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoDtoMapper todoDtoMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public TodoService todoService() {
            return mock(TodoService.class);
        }

        @Bean
        @Primary
        public TodoDtoMapper todoDtoMapper() {
            return mock(TodoDtoMapper.class);
        }
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        // Given
        Todo todo1 = new Todo("Todo 1", "Description 1");
        todo1.setId(1L);
        Todo todo2 = new Todo("Todo 2", "Description 2");
        todo2.setId(2L);
        List<Todo> todos = Arrays.asList(todo1, todo2);
        
        when(todoService.getAllTodos()).thenReturn(todos);
        when(todoDtoMapper.toDto(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            return com.example.todo.infrastructure.web.dto.TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
        });

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Todo 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Todo 2"));
    }

    @Test
    void shouldGetTodoById() throws Exception {
        // Given
        Long id = 1L;
        Todo todo = new Todo("Test Todo", "Test Description");
        todo.setId(id);
        
        when(todoService.getTodoById(id)).thenReturn(todo);
        when(todoDtoMapper.toDto(any(Todo.class))).thenAnswer(invocation -> {
            Todo t = invocation.getArgument(0);
            return com.example.todo.infrastructure.web.dto.TodoDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
        });

        // When & Then
        mockMvc.perform(get("/api/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void shouldCreateTodo() throws Exception {
        // Given
        CreateTodoRequest request = CreateTodoRequest.builder()
                .title("New Todo")
                .description("New Description")
                .build();
        
        Todo todo = new Todo("New Todo", "New Description");
        todo.setId(1L);
        
        when(todoService.createTodo(any(String.class), any(String.class))).thenReturn(todo);
        when(todoDtoMapper.toDto(any(Todo.class))).thenAnswer(invocation -> {
            Todo t = invocation.getArgument(0);
            return com.example.todo.infrastructure.web.dto.TodoDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
        });

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Todo"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        // Given
        Long id = 1L;
        CreateTodoRequest request = CreateTodoRequest.builder()
                .title("Updated Todo")
                .description("Updated Description")
                .build();
        
        Todo todo = new Todo("Updated Todo", "Updated Description");
        todo.setId(id);
        
        when(todoService.updateTodo(eq(id), any(String.class), any(String.class))).thenReturn(todo);
        when(todoDtoMapper.toDto(any(Todo.class))).thenAnswer(invocation -> {
            Todo t = invocation.getArgument(0);
            return com.example.todo.infrastructure.web.dto.TodoDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
        });

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Updated Todo"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void shouldToggleTodoCompletion() throws Exception {
        // Given
        Long id = 1L;
        Todo todo = new Todo("Test Todo", "Test Description");
        todo.setId(id);
        todo.setCompleted(true);
        
        when(todoService.toggleTodoCompletion(id)).thenReturn(todo);
        when(todoDtoMapper.toDto(any(Todo.class))).thenAnswer(invocation -> {
            Todo t = invocation.getArgument(0);
            return com.example.todo.infrastructure.web.dto.TodoDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
        });

        // When & Then
        mockMvc.perform(patch("/api/todos/{id}/toggle", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        // Given
        Long id = 1L;

        // When & Then
        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNoContent());
    }
}
