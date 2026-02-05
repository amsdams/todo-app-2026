export class TodoService {
  constructor() {
    this.apiUrl = '/api/todos';
  }

  async getAllTodos() {
    const response = await fetch(this.apiUrl);
    if (!response.ok) throw new Error('Failed to fetch todos');
    return response.json();
  }

  async getTodoById(id) {
    const response = await fetch(`${this.apiUrl}/${id}`);
    if (!response.ok) throw new Error('Failed to fetch todo');
    return response.json();
  }

  async createTodo(title, description) {
    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ title, description }),
    });
    if (!response.ok) throw new Error('Failed to create todo');
    return response.json();
  }

  async updateTodo(id, title, description) {
    const response = await fetch(`${this.apiUrl}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ title, description }),
    });
    if (!response.ok) throw new Error('Failed to update todo');
    return response.json();
  }

  async toggleTodoCompletion(id) {
    const response = await fetch(`${this.apiUrl}/${id}/toggle`, {
      method: 'PATCH',
    });
    if (!response.ok) throw new Error('Failed to toggle todo');
    return response.json();
  }

  async deleteTodo(id) {
    const response = await fetch(`${this.apiUrl}/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('Failed to delete todo');
  }
}
