import { LitElement, html, css } from 'lit';
import { TodoService } from './todo-service.js';

class TodoApp extends LitElement {
  static properties = {
    todos: { type: Array },
    newTitle: { type: String },
    newDescription: { type: String },
    editingTodo: { type: Object },
    editTitle: { type: String },
    editDescription: { type: String },
  };

  static styles = css`
    :host {
      display: block;
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
    }

    header {
      text-align: center;
      margin-bottom: 30px;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    h1 {
      margin: 0;
      font-size: 2.5rem;
    }

    .create-todo-section,
    .todo-list-section {
      background: white;
      padding: 25px;
      border-radius: 10px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      margin-bottom: 20px;
    }

    h2 {
      margin-top: 0;
      color: #333;
      border-bottom: 2px solid #667eea;
      padding-bottom: 10px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    input,
    textarea {
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 14px;
      font-family: inherit;
    }

    input:focus,
    textarea:focus {
      outline: none;
      border-color: #667eea;
    }

    textarea {
      resize: vertical;
      min-height: 80px;
    }

    button {
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 14px;
      font-weight: bold;
      transition: all 0.3s;
    }

    .btn-primary {
      background: #667eea;
      color: white;
    }

    .btn-primary:hover {
      background: #5568d3;
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(102, 126, 234, 0.4);
    }

    .btn-secondary {
      background: #6c757d;
      color: white;
    }

    .btn-secondary:hover {
      background: #5a6268;
    }

    .btn-danger {
      background: #dc3545;
      color: white;
    }

    .btn-danger:hover {
      background: #c82333;
    }

    .btn-small {
      padding: 6px 12px;
      font-size: 12px;
    }

    .empty-state {
      text-align: center;
      padding: 40px;
      color: #999;
    }

    .todo-list {
      display: flex;
      flex-direction: column;
      gap: 15px;
    }

    .todo-item {
      background: #f8f9fa;
      border: 1px solid #e9ecef;
      border-radius: 8px;
      padding: 15px;
      transition: all 0.3s;
    }

    .todo-item:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }

    .todo-item.completed {
      opacity: 0.7;
      background: #e8f5e9;
    }

    .todo-view,
    .todo-edit {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 15px;
    }

    .todo-content {
      display: flex;
      gap: 15px;
      flex: 1;
      align-items: flex-start;
    }

    input[type="checkbox"] {
      width: 20px;
      height: 20px;
      cursor: pointer;
      margin-top: 5px;
    }

    .todo-details {
      flex: 1;
    }

    .todo-details h3 {
      margin: 0 0 5px 0;
      color: #333;
      font-size: 18px;
    }

    .todo-details h3.strike-through {
      text-decoration: line-through;
      color: #999;
    }

    .todo-details p {
      margin: 5px 0;
      color: #666;
      font-size: 14px;
    }

    .timestamp {
      font-size: 12px;
      color: #999;
    }

    .todo-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
    }

    .todo-edit .form-group {
      flex: 1;
    }

    @media (max-width: 600px) {
      :host {
        padding: 10px;
      }

      h1 {
        font-size: 1.8rem;
      }

      .todo-view,
      .todo-edit {
        flex-direction: column;
      }

      .todo-actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
  `;

  constructor() {
    super();
    this.todos = [];
    this.newTitle = '';
    this.newDescription = '';
    this.editingTodo = null;
    this.editTitle = '';
    this.editDescription = '';
    this.todoService = new TodoService();
  }

  connectedCallback() {
    super.connectedCallback();
    this.loadTodos();
  }

  async loadTodos() {
    try {
      this.todos = await this.todoService.getAllTodos();
    } catch (error) {
      console.error('Error loading todos:', error);
      alert('Failed to load todos');
    }
  }

  async createTodo() {
    if (!this.newTitle.trim()) return;

    try {
      await this.todoService.createTodo(this.newTitle, this.newDescription);
      this.newTitle = '';
      this.newDescription = '';
      await this.loadTodos();
    } catch (error) {
      console.error('Error creating todo:', error);
      alert('Failed to create todo');
    }
  }

  startEdit(todo) {
    this.editingTodo = todo;
    this.editTitle = todo.title;
    this.editDescription = todo.description;
  }

  cancelEdit() {
    this.editingTodo = null;
    this.editTitle = '';
    this.editDescription = '';
  }

  async updateTodo() {
    if (!this.editTitle.trim()) return;

    try {
      await this.todoService.updateTodo(this.editingTodo.id, this.editTitle, this.editDescription);
      this.cancelEdit();
      await this.loadTodos();
    } catch (error) {
      console.error('Error updating todo:', error);
      alert('Failed to update todo');
    }
  }

  async toggleCompletion(todo) {
    try {
      await this.todoService.toggleTodoCompletion(todo.id);
      await this.loadTodos();
    } catch (error) {
      console.error('Error toggling todo:', error);
      alert('Failed to toggle todo');
    }
  }

  async deleteTodo(id) {
    if (!confirm('Are you sure you want to delete this todo?')) return;

    try {
      await this.todoService.deleteTodo(id);
      await this.loadTodos();
    } catch (error) {
      console.error('Error deleting todo:', error);
      alert('Failed to delete todo');
    }
  }

  formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString();
  }

  render() {
    return html`
      <header>
        <h1>Todo App</h1>
      </header>

      <div class="create-todo-section">
        <h2>Add New Todo</h2>
        <div class="form-group">
          <input
            type="text"
            placeholder="Todo title"
            .value=${this.newTitle}
            @input=${(e) => (this.newTitle = e.target.value)}
            @keyup=${(e) => e.key === 'Enter' && this.createTodo()}
          />
          <textarea
            placeholder="Description (optional)"
            .value=${this.newDescription}
            @input=${(e) => (this.newDescription = e.target.value)}
          ></textarea>
          <button class="btn-primary" @click=${this.createTodo}>Add Todo</button>
        </div>
      </div>

      <div class="todo-list-section">
        <h2>My Todos (${this.todos.length})</h2>

        ${this.todos.length === 0
          ? html`
              <div class="empty-state">
                <p>No todos yet. Add one above to get started!</p>
              </div>
            `
          : html`
              <div class="todo-list">
                ${this.todos.map(
                  (todo) => html`
                    <div class="todo-item ${todo.completed ? 'completed' : ''}">
                      ${this.editingTodo?.id === todo.id
                        ? html`
                            <div class="todo-edit">
                              <div class="form-group">
                                <input
                                  type="text"
                                  placeholder="Todo title"
                                  .value=${this.editTitle}
                                  @input=${(e) => (this.editTitle = e.target.value)}
                                />
                                <textarea
                                  placeholder="Description (optional)"
                                  .value=${this.editDescription}
                                  @input=${(e) => (this.editDescription = e.target.value)}
                                ></textarea>
                              </div>
                              <div class="todo-actions">
                                <button class="btn-primary btn-small" @click=${this.updateTodo}>Save</button>
                                <button class="btn-secondary btn-small" @click=${this.cancelEdit}>Cancel</button>
                              </div>
                            </div>
                          `
                        : html`
                            <div class="todo-view">
                              <div class="todo-content">
                                <input
                                  type="checkbox"
                                  ?checked=${todo.completed}
                                  @change=${() => this.toggleCompletion(todo)}
                                />
                                <div class="todo-details">
                                  <h3 class="${todo.completed ? 'strike-through' : ''}">${todo.title}</h3>
                                  ${todo.description ? html`<p>${todo.description}</p>` : ''}
                                  <span class="timestamp">Created: ${this.formatDate(todo.createdAt)}</span>
                                </div>
                              </div>
                              <div class="todo-actions">
                                <button class="btn-secondary btn-small" @click=${() => this.startEdit(todo)}>
                                  Edit
                                </button>
                                <button class="btn-danger btn-small" @click=${() => this.deleteTodo(todo.id)}>
                                  Delete
                                </button>
                              </div>
                            </div>
                          `}
                    </div>
                  `
                )}
              </div>
            `}
      </div>
    `;
  }
}

customElements.define('todo-app', TodoApp);
