package com.example.todo.infrastructure.persistence.repository;

import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaTodoRepository extends JpaRepository<TodoEntity, UUID> {
}
