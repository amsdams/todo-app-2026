package com.example.todo.domain.port;

import com.example.todo.domain.model.Todo;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for Todo persistence
 */
public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Todo> findCompletedTodos();
    void deleteAll(List<Todo> todos);
}
