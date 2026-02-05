package com.example.todo.application.service;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service (Use Cases)
 * This is the inbound port implementation
 */
@Service
@Transactional
public class TodoService {
    
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(String title, String description) {
        Todo todo = new Todo(title, description);
        return todoRepository.save(todo);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
    }

    public Todo updateTodo(Long id, String title, String description) {
        Todo todo = getTodoById(id);
        todo.updateDetails(title, description);
        return todoRepository.save(todo);
    }

    public Todo toggleTodoCompletion(Long id) {
        Todo todo = getTodoById(id);
        if (todo.isCompleted()) {
            todo.markAsIncomplete();
        } else {
            todo.markAsCompleted();
        }
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }
}
