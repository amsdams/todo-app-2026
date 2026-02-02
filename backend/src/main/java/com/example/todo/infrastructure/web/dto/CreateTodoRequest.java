package com.example.todo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating or updating a todo")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTodoRequest {
	@Schema(description = "Title of the todo", example = "Buy groceries", requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;
	@Schema(description = "Detailed description of the todo", example = "Milk, eggs, bread, cheese")
    private String description;

}
