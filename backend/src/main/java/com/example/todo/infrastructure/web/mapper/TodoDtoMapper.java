package com.example.todo.infrastructure.web.mapper;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.TodoDto;
import org.springframework.stereotype.Component;

@Component
public class TodoDtoMapper {

    public TodoDto toDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.isCompleted());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        return dto;
    }
}
