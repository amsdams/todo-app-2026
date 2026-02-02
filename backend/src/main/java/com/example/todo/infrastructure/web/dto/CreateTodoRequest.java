package com.example.todo.infrastructure.web.dto;

public class CreateTodoRequest {
    private String title;
    private String description;

    public CreateTodoRequest() {
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
}
