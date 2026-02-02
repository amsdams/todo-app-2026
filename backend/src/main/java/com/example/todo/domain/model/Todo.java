package com.example.todo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Todo {
    private UUID id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Todo() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.completed = false;
    }

    public Todo(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public void markAsCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsIncomplete() {
        this.completed = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String title, String description) {
        this.title = title;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
