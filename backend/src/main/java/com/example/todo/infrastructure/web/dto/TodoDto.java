package com.example.todo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo data transfer object")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    // Getters and Setters
     
    @Schema(description = "Unique identifier of the todo", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Title of the todo", example = "Buy groceries", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    
    @Schema(description = "Detailed description of the todo", example = "Milk, eggs, bread")
    private String description;
    
    @Schema(description = "Completion status of the todo", example = "false")
    private boolean completed;
    
    @Schema(description = "Timestamp when the todo was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the todo was last updated", example = "2024-01-15T14:20:00")
    private LocalDateTime updatedAt;
}
