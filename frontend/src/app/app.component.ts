import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TodoService } from './services/todo.service';
import { Todo, CreateTodoRequest } from './models/todo.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Todo App';
  todos: Todo[] = [];
  newTodo: CreateTodoRequest = { title: '', description: '' };
  editingTodo: Todo | null = null;
  editForm: CreateTodoRequest = { title: '', description: '' };

  constructor(private todoService: TodoService) {}

  ngOnInit(): void {
    this.loadTodos();
  }

  loadTodos(): void {
    this.todoService.getAllTodos().subscribe({
      next: (todos) => {
        this.todos = todos;
      },
      error: (error) => {
        console.error('Error loading todos:', error);
      }
    });
  }

  createTodo(): void {
    if (this.newTodo.title.trim()) {
      this.todoService.createTodo(this.newTodo).subscribe({
        next: (todo) => {
          this.todos.push(todo);
          this.newTodo = { title: '', description: '' };
        },
        error: (error) => {
          console.error('Error creating todo:', error);
        }
      });
    }
  }

  startEdit(todo: Todo): void {
    this.editingTodo = todo;
    this.editForm = {
      title: todo.title,
      description: todo.description
    };
  }

  cancelEdit(): void {
    this.editingTodo = null;
    this.editForm = { title: '', description: '' };
  }

  updateTodo(): void {
    if (this.editingTodo && this.editForm.title.trim()) {
      this.todoService.updateTodo(this.editingTodo.id, this.editForm).subscribe({
        next: (updatedTodo) => {
          const index = this.todos.findIndex(t => t.id === updatedTodo.id);
          if (index !== -1) {
            this.todos[index] = updatedTodo;
          }
          this.cancelEdit();
        },
        error: (error) => {
          console.error('Error updating todo:', error);
        }
      });
    }
  }

  toggleCompletion(todo: Todo): void {
    this.todoService.toggleTodoCompletion(todo.id).subscribe({
      next: (updatedTodo) => {
        const index = this.todos.findIndex(t => t.id === updatedTodo.id);
        if (index !== -1) {
          this.todos[index] = updatedTodo;
        }
      },
      error: (error) => {
        console.error('Error toggling todo:', error);
      }
    });
  }

  deleteTodo(id: string): void {
    if (confirm('Are you sure you want to delete this todo?')) {
      this.todoService.deleteTodo(id).subscribe({
        next: () => {
          this.todos = this.todos.filter(t => t.id !== id);
        },
        error: (error) => {
          console.error('Error deleting todo:', error);
        }
      });
    }
  }
}
