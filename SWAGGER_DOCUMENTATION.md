# Swagger UI / OpenAPI Documentation

## Overview

The Todo API now includes interactive API documentation using Swagger UI (OpenAPI 3.0). This allows you to explore and test all API endpoints directly from your browser.

## Accessing Swagger UI

After starting the backend application, you can access Swagger UI at:

**Swagger UI**: http://localhost:8080/swagger-ui.html

**OpenAPI JSON**: http://localhost:8080/api-docs

## Features

### Interactive API Testing
- **Try Out Endpoints**: Test all API operations directly from the browser
- **Request/Response Examples**: See sample requests and responses
- **Schema Documentation**: View detailed object models
- **Response Codes**: Understand all possible HTTP status codes

### API Endpoints Documentation

All endpoints are documented with:
- **Operation Summary**: Brief description of what the endpoint does
- **Parameters**: Required and optional parameters with descriptions
- **Request Body**: Expected JSON structure with examples
- **Responses**: All possible response codes with descriptions
- **Models**: Detailed schema of request/response objects

## Available Endpoints

### Todo Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/todos` | Get all todos |
| `GET` | `/api/todos/{id}` | Get a specific todo by ID |
| `POST` | `/api/todos` | Create a new todo |
| `PUT` | `/api/todos/{id}` | Update an existing todo |
| `PATCH` | `/api/todos/{id}/toggle` | Toggle todo completion status |
| `DELETE` | `/api/todos/{id}` | Delete a todo |

## Using Swagger UI

### 1. Explore Endpoints
- Navigate to http://localhost:8080/swagger-ui.html
- You'll see all available endpoints grouped by tags
- Click on any endpoint to expand its documentation

### 2. Try an Endpoint

#### Example: Create a Todo

1. Click on `POST /api/todos`
2. Click the **"Try it out"** button
3. Edit the request body:
```json
{
  "title": "Test from Swagger",
  "description": "Created using Swagger UI"
}
```
4. Click **"Execute"**
5. View the response below, including:
   - Response code (201 Created)
   - Response body with the created todo
   - Response headers

#### Example: Get All Todos

1. Click on `GET /api/todos`
2. Click **"Try it out"**
3. Click **"Execute"**
4. View the list of all todos

#### Example: Toggle Completion

1. Copy a todo ID from a previous response
2. Click on `PATCH /api/todos/{id}/toggle`
3. Click **"Try it out"**
4. Paste the todo ID in the `id` field
5. Click **"Execute"**
6. See the todo with updated completion status

### 3. View Schemas

Scroll down to the **"Schemas"** section to see:
- **TodoDto**: Response model structure
- **CreateTodoRequest**: Request model structure

Each schema shows:
- Field names
- Data types
- Descriptions
- Example values
- Required/optional status

## Configuration

### OpenAPI Configuration

The API documentation is configured in `OpenAPIConfig.java`:

```java
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI todoOpenAPI() {
        // Configuration for API info, servers, etc.
    }
}
```

### Application Properties

Swagger settings in `application.properties`:

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

### Annotations Used

#### Controller Level
- `@Tag`: Groups related endpoints
- `@Operation`: Describes the endpoint operation
- `@ApiResponses`: Documents all possible responses
- `@Parameter`: Describes path/query parameters

#### DTO Level
- `@Schema`: Documents the object model and its properties

## Example API Call from Swagger

### Creating a Todo

**Request:**
```http
POST /api/todos HTTP/1.1
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Add README and API docs"
}
```

**Response (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Complete project documentation",
  "description": "Add README and API docs",
  "completed": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## Benefits of Swagger UI

### For Developers
- ✅ **Interactive Testing**: Test endpoints without external tools
- ✅ **Up-to-Date Documentation**: Auto-generated from code
- ✅ **Clear Contracts**: Understand request/response structures
- ✅ **Quick Prototyping**: Rapid API testing during development

### For Frontend Developers
- ✅ **API Discovery**: See all available endpoints
- ✅ **Type Information**: Know exact data structures
- ✅ **Example Values**: Understand expected formats
- ✅ **Error Codes**: Handle all response scenarios

### For API Consumers
- ✅ **Self-Service**: Explore API without asking developers
- ✅ **Live Testing**: Verify API behavior in real-time
- ✅ **Documentation**: Always in sync with implementation

## Advanced Features

### Customization Options

You can customize Swagger UI by modifying `application.properties`:

```properties
# Enable/disable try-it-out by default
springdoc.swagger-ui.tryItOutEnabled=true

# Display request duration
springdoc.swagger-ui.displayRequestDuration=true

# Enable deep linking
springdoc.swagger-ui.deepLinking=true

# Configure default model expand depth
springdoc.swagger-ui.defaultModelsExpandDepth=1

# Show/hide model structure
springdoc.swagger-ui.defaultModelExpandDepth=1
```

### Exporting OpenAPI Spec

You can export the OpenAPI specification:

```bash
# Download OpenAPI JSON
curl http://localhost:8080/api-docs > openapi.json

# Download OpenAPI YAML
curl http://localhost:8080/api-docs.yaml > openapi.yaml
```

Use these files to:
- Generate client SDKs
- Import into Postman
- Share with external teams
- Generate documentation sites

## Security Notes

In production, you might want to:

1. **Disable Swagger UI** in production:
```properties
springdoc.swagger-ui.enabled=false
```

2. **Protect with Authentication**:
```java
@Configuration
public class SecurityConfig {
    // Add authentication for /swagger-ui.html
}
```

3. **Hide Internal Endpoints**:
```java
@Hidden  // Add to methods you don't want in docs
public ResponseEntity<?> internalEndpoint() { }
```

## Troubleshooting

### Swagger UI Not Loading
- Check if backend is running on port 8080
- Verify URL: http://localhost:8080/swagger-ui.html
- Check browser console for errors

### Missing Endpoints
- Ensure `@RestController` annotation is present
- Check if methods have HTTP mapping annotations
- Verify component scanning is configured

### Incorrect Schemas
- Check `@Schema` annotations on DTOs
- Verify getter/setter methods exist
- Ensure proper JSON serialization

## Integration with Frontend

Your Angular app can consume the OpenAPI spec:

```typescript
// Generate TypeScript client
npm install @openapitools/openapi-generator-cli

// Generate from spec
openapi-generator-cli generate \
  -i http://localhost:8080/api-docs \
  -g typescript-angular \
  -o src/app/generated
```

## Resources

- **Springdoc OpenAPI**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/
- **Swagger UI**: https://swagger.io/tools/swagger-ui/

## Quick Reference

| URL | Purpose |
|-----|---------|
| http://localhost:8080/swagger-ui.html | Interactive API documentation |
| http://localhost:8080/api-docs | OpenAPI JSON specification |
| http://localhost:8080/api-docs.yaml | OpenAPI YAML specification |

---

Now you have comprehensive, interactive API documentation! 🎉
