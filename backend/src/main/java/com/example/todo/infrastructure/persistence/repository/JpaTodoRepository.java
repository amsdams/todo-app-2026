package com.example.todo.infrastructure.persistence.repository;

import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTodoRepository extends JpaRepository<TodoEntity, Long> {
    
    @Query("SELECT t FROM TodoEntity t WHERE t.completed = true")
    List<TodoEntity> findCompletedTodos();
}
