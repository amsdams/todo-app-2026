package com.example.todo.infrastructure.web.controller;

import com.example.todo.application.service.TodoService;
import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.CreateTodoRequest;
import com.example.todo.infrastructure.web.dto.TodoDto;
import com.example.todo.infrastructure.web.mapper.TodoDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Inbound adapter for REST API
 */
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:4200")
public class TodoController {

    private final TodoService todoService;
    private final TodoDtoMapper todoDtoMapper;

    public TodoController(TodoService todoService, TodoDtoMapper todoDtoMapper) {
        this.todoService = todoService;
        this.todoDtoMapper = todoDtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        List<TodoDto> todos = todoService.getAllTodos().stream()
                .map(todoDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable UUID id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody CreateTodoRequest request) {
        Todo todo = todoService.createTodo(request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoDtoMapper.toDto(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable UUID id,
            @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.updateTodo(id, request.getTitle(), request.getDescription());
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDto> toggleTodoCompletion(@PathVariable UUID id) {
        Todo todo = todoService.toggleTodoCompletion(id);
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
