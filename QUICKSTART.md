# Quick Start Guide

## Prerequisites Check

Before starting, ensure you have:

```bash
# Check Java version (need 21+)
java -version

# Check Maven version (need 3.6+)
mvn -version

# Check Node.js version (need 18+)
node -version

# Check npm version
npm -version

# Install Angular CLI globally if not installed
npm install -g @angular/cli
```

## Quick Setup (5 Minutes)

### Step 1: Start the Backend

```bash
# Navigate to backend directory
cd backend

# Run Spring Boot application
mvn spring-boot:run
```

Wait for the message: **"Started TodoApplication"**

The backend is now running at `http://localhost:8080`

### Step 2: Start the Frontend

Open a **new terminal** window:

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start Vite dev server
npm run dev
```

Wait for the message: **"Local: http://localhost:4200/"**

### Step 3: Open the Application

Open your browser and go to: `http://localhost:4200`

You should see the Todo App interface!

## Test the Application

### Create a Todo
1. Enter a title: "Buy groceries"
2. Enter a description: "Milk, eggs, bread"
3. Click "Add Todo"

### Mark as Complete
- Click the checkbox next to the todo

### Edit a Todo
- Click "Edit" button
- Modify the title or description
- Click "Save"

### Delete a Todo
- Click "Delete" button
- Confirm the deletion

## Verify Backend API

You can test the API directly:

```bash
# Get all todos
curl http://localhost:8080/api/todos

# Create a todo
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Test Todo", "description": "Testing API"}'
```

### Using Swagger UI (Recommended)

The easiest way to test the API is using Swagger UI:

1. Go to `http://localhost:8080/swagger-ui.html`
2. You'll see interactive API documentation
3. Click on any endpoint (e.g., `POST /api/todos`)
4. Click **"Try it out"**
5. Modify the request body if needed
6. Click **"Execute"** to test the endpoint
7. See the response immediately

**Benefits:**
- ✅ Interactive testing without command line
- ✅ See all available endpoints
- ✅ View request/response examples
- ✅ Understand data models

See [SWAGGER_DOCUMENTATION.md](SWAGGER_DOCUMENTATION.md) for complete guide.

## Access H2 Database Console

1. Go to `http://localhost:8080/h2-console`
2. Use these settings:
   - JDBC URL: `jdbc:h2:mem:tododb`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect"

You can now see your todos in the database!

## Generate Test Coverage Report

Want to see how well the code is tested?

```bash
cd backend
mvn clean test
```

Then open the coverage report:

```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

**What You'll See:**

- 📊 Overall coverage: ~90%
- 🟢 Green: Well-tested code
- 🟡 Yellow: Partially tested
- 🔴 Red: Untested code

See [TEST_COVERAGE.md](TEST_COVERAGE.md) for more details.

## Troubleshooting

### Backend won't start
- Ensure Java 21+ is installed
- Check if port 8080 is already in use
- Run `mvn clean install` first

### Frontend won't start
- Ensure Node.js 18+ is installed
- Delete `node_modules` and run `npm install` again
- Check if port 4200 is already in use

### CORS errors
- Make sure backend is running on port 8080
- Make sure frontend is running on port 4200
- Check `@CrossOrigin` annotation in TodoController

### Can't connect to backend
- Verify backend is running: `http://localhost:8080/api/todos`
- Check browser console for errors
- Ensure the API URL in `todo.service.ts` is correct

## Project Structure at a Glance

```
todo-app/
├── backend/           # Spring Boot API (Port 8080)
│   └── src/main/java/com/example/todo/
│       ├── domain/           # Business logic
│       ├── application/      # Use cases
│       └── infrastructure/   # Adapters
└── frontend/          # Angular UI (Port 4200)
    └── src/app/
        ├── models/           # TypeScript interfaces
        ├── services/         # HTTP services
        └── app.component.*   # Main component
```

## Next Steps

1. Read `README.md` for complete documentation
2. Read `HEXAGONAL_ARCHITECTURE.md` to understand the architecture
3. Try adding new features:
   - Add todo categories
   - Add due dates
   - Add priority levels
   - Add search functionality

## Stopping the Application

### Stop Backend
Press `Ctrl+C` in the backend terminal

### Stop Frontend
Press `Ctrl+C` in the frontend terminal

## Clean Build (If Needed)

```bash
# Backend
cd backend
mvn clean

# Frontend
cd frontend
rm -rf node_modules dist
npm install
```

Enjoy building with Hexagonal Architecture! 🎉
