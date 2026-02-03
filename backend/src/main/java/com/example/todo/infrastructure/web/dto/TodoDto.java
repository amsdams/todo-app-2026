package com.example.todo.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Todo data transfer object")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {
    
    @Schema(description = "Unique identifier of the todo", example = "1")
    private Long id;
    
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
