package com.example.todo.infrastructure.persistence.adapter;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import com.example.todo.infrastructure.persistence.mapper.TodoMapper;
import com.example.todo.infrastructure.persistence.repository.JpaTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Outbound adapter for persistence
 */
@Component
@RequiredArgsConstructor
public class TodoPersistenceAdapter implements TodoRepository {

    private final JpaTodoRepository jpaTodoRepository;
    private final TodoMapper todoMapper;

    @Override
    public Todo save(Todo todo) {
        TodoEntity entity = todoMapper.toEntity(todo);
        TodoEntity saved = jpaTodoRepository.save(entity);
        return todoMapper.toDomain(saved);
    }

    @Override
    public Optional<Todo> findById(Long id) {
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
    public void deleteById(Long id) {
        jpaTodoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaTodoRepository.existsById(id);
    }

    @Override
    public List<Todo> findCompletedTodos() {
        return jpaTodoRepository.findCompletedTodos().stream()
                .map(todoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll(List<Todo> todos) {
        List<TodoEntity> entities = todos.stream()
                .map(todoMapper::toEntity)
                .collect(Collectors.toList());
        jpaTodoRepository.deleteAll(entities);
    }
}
