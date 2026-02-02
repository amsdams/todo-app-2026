package com.example.todo.infrastructure.persistence.adapter;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import com.example.todo.infrastructure.persistence.mapper.TodoMapper;
import com.example.todo.infrastructure.persistence.repository.JpaTodoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Outbound adapter for persistence
 */
@Component
public class TodoPersistenceAdapter implements TodoRepository {

    private final JpaTodoRepository jpaTodoRepository;
    private final TodoMapper todoMapper;

    public TodoPersistenceAdapter(JpaTodoRepository jpaTodoRepository, TodoMapper todoMapper) {
        this.jpaTodoRepository = jpaTodoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    public Todo save(Todo todo) {
        TodoEntity entity = todoMapper.toEntity(todo);
        TodoEntity saved = jpaTodoRepository.save(entity);
        return todoMapper.toDomain(saved);
    }

    @Override
    public Optional<Todo> findById(UUID id) {
        return jpaTodoRepository.findById(id)
                .map(todoMapper::toDomain);
    }

    @Override
    public List<Todo> findAll() {
        return jpaTodoRepository.findAll().stream()
                .map(todoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaTodoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaTodoRepository.existsById(id);
    }
}
