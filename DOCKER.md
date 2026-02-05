# Docker Usage Guide

## Quick Start

### Prerequisites

- Docker 20.10+ installed
- Docker Compose 2.0+ installed
- 2GB free disk space
- Ports 80 and 8080 available

### 1. Build and Start

```bash
# Build and start all services
docker-compose up --build -d

# Wait for services to be healthy (30-60 seconds)
docker-compose ps
```

### 2. Access Application

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

### 3. View Logs

```bash
# All services
docker-compose logs -f

# Backend only
docker-compose logs -f backend

# Frontend only
docker-compose logs -f frontend
```

### 4. Stop Services

```bash
# Stop (keeps data)
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Detailed Commands

### Build Commands

```bash
# Build all services
docker-compose build

# Build with no cache (clean build)
docker-compose build --no-cache

# Build specific service
docker-compose build backend
docker-compose build frontend

# Build with parallel execution
docker-compose build --parallel
```

### Start/Stop Commands

```bash
# Start in foreground (see logs)
docker-compose up

# Start in background
docker-compose up -d

# Start specific service
docker-compose up -d backend

# Stop all services
docker-compose down

# Stop and remove volumes/networks
docker-compose down -v

# Restart service
docker-compose restart backend
```

### Monitoring Commands

```bash
# Check service status
docker-compose ps

# View logs (last 100 lines)
docker-compose logs --tail=100

# Follow logs in real-time
docker-compose logs -f

# View service logs since specific time
docker-compose logs --since 30m backend

# Check container resource usage
docker stats todo-backend todo-frontend
```

### Debugging Commands

```bash
# Execute command in running container
docker-compose exec backend sh
docker-compose exec frontend sh

# View container environment variables
docker-compose exec backend env

# Check files in container
docker-compose exec backend ls -la /app
docker-compose exec frontend ls -la /usr/share/nginx/html

# Test backend health
docker-compose exec backend wget -O- http://localhost:8080/actuator/health
```

### Maintenance Commands

```bash
# Remove stopped containers
docker-compose rm

# Remove all unused Docker resources
docker system prune

# Remove all unused images
docker image prune -a

# View Docker disk usage
docker system df
```

## Configuration

### Environment Variables

Create `.env` file in project root:

```env
# Backend
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xmx512m -Xms256m

# Ports
BACKEND_PORT=8080
FRONTEND_PORT=80
```

Then update `docker-compose.yml`:

```yaml
services:
  backend:
    ports:
      - "${BACKEND_PORT}:8080"
    env_file:
      - .env
```

### Port Mapping

Change external ports in `docker-compose.yml`:

```yaml
services:
  backend:
    ports:
      - "8081:8080"  # Access backend on port 8081
  
  frontend:
    ports:
      - "8000:80"    # Access frontend on port 8000
```

### Resource Limits

Add resource limits:

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

## Health Checks

### View Health Status

```bash
# Check if services are healthy
docker-compose ps

# Expected output:
# NAME            STATUS                    PORTS
# todo-backend    Up (healthy)             0.0.0.0:8080->8080/tcp
# todo-frontend   Up (healthy)             0.0.0.0:80->80/tcp
```

### Test Endpoints

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost/

# API through frontend proxy
curl http://localhost/api/todos
```

### Health Check Configuration

In `docker-compose.yml`:

```yaml
healthcheck:
  test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s      # Check every 30 seconds
  timeout: 10s       # Timeout after 10 seconds
  retries: 5         # Retry 5 times before marking unhealthy
  start_period: 60s  # Grace period on startup
```

## Troubleshooting

### Services Won't Start

```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend

# Common issues:
# 1. Port already in use
# 2. Build failed
# 3. Health check failing
```

See [DOCKER_TROUBLESHOOTING.md](DOCKER_TROUBLESHOOTING.md) for detailed solutions.

### Rebuild After Code Changes

```bash
# Rebuild and restart
docker-compose up --build --force-recreate

# Or step by step
docker-compose down
docker-compose build
docker-compose up -d
```

### Clean Start

```bash
# Complete clean restart
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## Production Deployment

### Using Docker Compose in Production

```bash
# Start with production compose file
docker-compose -f docker-compose.prod.yml up -d

# Scale backend service
docker-compose up -d --scale backend=3

# View scaled services
docker-compose ps
```

### Using Pre-built Images

Pull from GitHub Container Registry:

```bash
# Pull images
docker pull ghcr.io/<username>/todo-app-backend:latest
docker pull ghcr.io/<username>/todo-app-frontend:latest

# Run with pulled images
docker run -d -p 8080:8080 ghcr.io/<username>/todo-app-backend:latest
docker run -d -p 80:80 ghcr.io/<username>/todo-app-frontend:latest
```

### Docker Swarm Deployment

```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml todo-app

# List services
docker stack services todo-app

# Remove stack
docker stack rm todo-app
```

## Performance Optimization

### Enable BuildKit

```bash
# Set environment variables
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

# Build with BuildKit
docker-compose build
```

**Benefits:**

- Faster builds
- Better caching
- Parallel layer downloads
- Smaller images

### Build Cache

Docker automatically caches layers. To maximize cache hits:

1. Dependencies are copied first (rarely change)
2. Source code copied last (changes frequently)
3. Multi-stage builds used (smaller final images)

### Network Performance

For better networking:

```yaml
networks:
  todo-network:
    driver: bridge
    driver_opts:
      com.docker.network.driver.mtu: 1450
```

## Monitoring & Logs

### View Resource Usage

```bash
# Real-time stats
docker stats

# One-time snapshot
docker stats --no-stream

# Specific containers
docker stats todo-backend todo-frontend
```

### Log Management

```bash
# View logs with timestamps
docker-compose logs -f -t

# Export logs to file
docker-compose logs > logs.txt

# Clear logs (restart containers)
docker-compose restart
```

### Log Rotation

Already configured in `docker-compose.yml`:

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"    # Max file size
    max-file: "3"       # Keep 3 files
```

## Security

### Non-Root User

Both containers run as non-root:

**Backend:**

```dockerfile
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
```

**Frontend:**

- nginx user (default non-root)

### Security Headers

Frontend includes security headers:

```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
```

### Scan for Vulnerabilities

```bash
# Scan images with Trivy
docker run --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image todo-backend

# Scan with Docker Scout
docker scout cves todo-backend
```

## Networking

### Access Container by Name

Containers can communicate using service names:

**From backend:**

```java
// Not needed - backend doesn't call frontend
```

**From frontend:**

```nginx
# In nginx.conf
location /api/ {
    proxy_pass http://backend:8080;  # ← Service name
}
```

### Expose Additional Ports

```yaml
services:
  backend:
    ports:
      - "8080:8080"    # Application
      - "8081:8081"    # Additional port
```

### Custom Network Configuration

```yaml
networks:
  todo-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.28.0.0/16
```

## Integration with CI/CD

The `docker-compose.yml` file is used by GitHub Actions for:

1. Local testing
2. Integration testing
3. Building images
4. Pushing to registry

See [CICD.md](CICD.md) for CI/CD integration details.

## Backup & Restore

### Backup Application State

```bash
# Stop services
docker-compose down

# Create backup directory
mkdir backup

# Backup volumes (if using persistent volumes)
docker run --rm -v todo-app_data:/data -v $(pwd)/backup:/backup \
  alpine tar czf /backup/data-backup.tar.gz /data

# Restart services
docker-compose up -d
```

### Restore from Backup

```bash
# Stop services
docker-compose down

# Restore volumes
docker run --rm -v todo-app_data:/data -v $(pwd)/backup:/backup \
  alpine tar xzf /backup/data-backup.tar.gz -C /

# Restart services
docker-compose up -d
```

## Development Workflow

### Hot Reload Setup

For development with hot reload:

```yaml
# docker-compose.dev.yml
services:
  backend:
    volumes:
      - ./backend/src:/app/src
    command: mvn spring-boot:run
  
  frontend:
    volumes:
      - ./frontend/src:/app/src
    command: npm run dev
```

Use development compose file:

```bash
docker-compose -f docker-compose.dev.yml up
```

## Quick Reference

### Essential Commands

```bash
# Start
docker-compose up -d

# Logs
docker-compose logs -f

# Status
docker-compose ps

# Stop
docker-compose down

# Rebuild
docker-compose up --build

# Clean rebuild
docker-compose down -v && docker-compose up --build
```

### Service URLs

| Service    | URL                                   | Description       |
|------------|---------------------------------------|-------------------|
| Frontend   | http://localhost                      | Main application  |
| Backend    | http://localhost:8080                 | REST API          |
| Swagger    | http://localhost:8080/swagger-ui.html | API documentation |
| H2 Console | http://localhost:8080/h2-console      | Database console  |

### Container Names

- `todo-backend` - Spring Boot application
- `todo-frontend` - Nginx + Lit frontend

## Summary

Docker Compose provides:

- ✅ One-command deployment
- ✅ Consistent environments
- ✅ Easy service orchestration
- ✅ Built-in health checks
- ✅ Log management
- ✅ Network isolation

**Start the application:**

```bash
docker-compose up -d
```

**View it running:**
http://localhost

That's it! 🚀
