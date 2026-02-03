package com.example.todo.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request object for creating or updating a todo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTodoRequest {
    
    @Schema(description = "Title of the todo", example = "Buy groceries", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    
    @Schema(description = "Detailed description of the todo", example = "Milk, eggs, bread, cheese")
    private String description;
}
