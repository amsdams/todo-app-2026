package com.example.todo.infrastructure.web.mapper;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.web.dto.TodoDto;
import org.springframework.stereotype.Component;

@Component
public class TodoDtoMapper {

    public TodoDto toDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }
}
