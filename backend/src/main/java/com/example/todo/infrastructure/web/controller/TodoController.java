package com.example.todo.infrastructure.web.controller;

import com.example.todo.application.service.TodoService;
import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.CreateTodoRequest;
import com.example.todo.infrastructure.web.dto.TodoDto;
import com.example.todo.infrastructure.web.mapper.TodoDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inbound adapter for REST API
 */
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Todo Management", description = "APIs for managing todos")
public class TodoController {

    private final TodoService todoService;
    private final TodoDtoMapper todoDtoMapper;

    public TodoController(TodoService todoService, TodoDtoMapper todoDtoMapper) {
        this.todoService = todoService;
        this.todoDtoMapper = todoDtoMapper;
    }

    @Operation(
        summary = "Get all todos",
        description = "Retrieves a list of all todos in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved list of todos",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoDto.class))
    )
    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        List<TodoDto> todos = todoService.getAllTodos().stream()
                .map(todoDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(todos);
    }

    @Operation(
        summary = "Get todo by ID",
        description = "Retrieves a specific todo by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved todo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(
            @Parameter(description = "ID of the todo to retrieve", required = true)
            @PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @Operation(
        summary = "Create a new todo",
        description = "Creates a new todo with the provided title and description"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Todo created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(
            @Parameter(description = "Todo creation request with title and description", required = true)
            @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.createTodo(request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoDtoMapper.toDto(todo));
    }

    @Operation(
        summary = "Update a todo",
        description = "Updates an existing todo's title and description"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Todo updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @Parameter(description = "ID of the todo to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Todo update request with new title and description", required = true)
            @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.updateTodo(id, request.getTitle(), request.getDescription());
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @Operation(
        summary = "Toggle todo completion status",
        description = "Toggles the completion status of a todo (completed ↔ incomplete)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Todo completion status toggled successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content
        )
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDto> toggleTodoCompletion(
            @Parameter(description = "ID of the todo to toggle", required = true)
            @PathVariable Long id) {
        Todo todo = todoService.toggleTodoCompletion(id);
        return ResponseEntity.ok(todoDtoMapper.toDto(todo));
    }

    @Operation(
        summary = "Delete a todo",
        description = "Permanently deletes a todo from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Todo deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "ID of the todo to delete", required = true)
            @PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
