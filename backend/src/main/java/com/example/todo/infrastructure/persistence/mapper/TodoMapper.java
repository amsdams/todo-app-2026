package com.example.todo.infrastructure.persistence.mapper;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public TodoEntity toEntity(Todo todo) {
        TodoEntity entity = new TodoEntity();
        entity.setId(todo.getId());
        entity.setTitle(todo.getTitle());
        entity.setDescription(todo.getDescription());
        entity.setCompleted(todo.isCompleted());
        entity.setCreatedAt(todo.getCreatedAt());
        entity.setUpdatedAt(todo.getUpdatedAt());
        return entity;
    }

    public Todo toDomain(TodoEntity entity) {
        Todo todo = new Todo();
        todo.setId(entity.getId());
        todo.setTitle(entity.getTitle());
        todo.setDescription(entity.getDescription());
        todo.setCompleted(entity.isCompleted());
        todo.setCreatedAt(entity.getCreatedAt());
        todo.setUpdatedAt(entity.getUpdatedAt());
        return todo;
    }
}
