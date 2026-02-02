import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Todo, CreateTodoRequest } from '../models/todo.model';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private apiUrl = 'http://localhost:8080/api/todos';

  constructor(private http: HttpClient) {}

  getAllTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(this.apiUrl);
  }

  getTodoById(id: string): Observable<Todo> {
    return this.http.get<Todo>(`${this.apiUrl}/${id}`);
  }

  createTodo(request: CreateTodoRequest): Observable<Todo> {
    return this.http.post<Todo>(this.apiUrl, request);
  }

  updateTodo(id: string, request: CreateTodoRequest): Observable<Todo> {
    return this.http.put<Todo>(`${this.apiUrl}/${id}`, request);
  }

  toggleTodoCompletion(id: string): Observable<Todo> {
    return this.http.patch<Todo>(`${this.apiUrl}/${id}/toggle`, {});
  }

  deleteTodo(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
