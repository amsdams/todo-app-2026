package com.example.todo.domain.port;

import com.example.todo.domain.model.Todo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Todo persistence
 */
public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(UUID id);
    List<Todo> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
