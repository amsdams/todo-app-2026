# Docker Troubleshooting Guide

## Common Issues and Solutions

### Issue 1: npm ci fails in frontend Dockerfile

**Error:**

```
failed to solve: process "/bin/sh -c npm ci --only=production" did not complete successfully: exit code: 1
```

**Causes:**

1. Missing `package-lock.json`
2. Corrupted `package-lock.json`
3. Using `--only=production` but `vite` is in `devDependencies`

**Solutions:**

#### Solution 1: Regenerate package-lock.json

```bash
cd frontend
rm package-lock.json
npm install
git add package-lock.json
git commit -m "Regenerate package-lock.json"
```

#### Solution 2: Use the updated Dockerfile

The Dockerfile has been updated to use `npm ci` without the `--only=production` flag since Vite (a dev dependency) is
needed for the build.

#### Solution 3: Clean Docker cache

```bash
docker-compose down -v
docker system prune -a
docker-compose up --build
```

### Issue 2: Backend build fails

**Error:**

```
failed to solve: process "/bin/sh -c mvn clean package -DskipTests -B" did not complete successfully
```

**Solutions:**

#### Solution 1: Build locally first

```bash
cd backend
mvn clean package -DskipTests
```

Fix any compilation errors, then try Docker again.

#### Solution 2: Check Java version

Ensure the Dockerfile uses Java 21:

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build
```

#### Solution 3: Check pom.xml

Verify `pom.xml` is valid XML and all dependencies are accessible.

### Issue 3: Port already in use

**Error:**

```
Error starting userland proxy: listen tcp4 0.0.0.0:8080: bind: address already in use
```

**Solutions:**

#### Solution 1: Stop conflicting service

```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

#### Solution 2: Change port in docker-compose.yml

```yaml
services:
  backend:
    ports:
      - "8081:8080"  # External:Internal
```

### Issue 4: Backend not accessible from frontend

**Error:**
Frontend shows network errors when calling API

**Solutions:**

#### Solution 1: Check backend health

```bash
docker-compose ps
docker-compose logs backend
```

#### Solution 2: Verify network configuration

```bash
docker network ls
docker network inspect todo-app_todo-network
```

#### Solution 3: Wait for backend to start

The frontend depends on backend health check. Wait 30-40 seconds for backend to be healthy.

### Issue 5: Build context too large

**Error:**

```
sending build context to Docker daemon  XXX MB
```

**Solutions:**

#### Solution 1: Check .dockerignore

Ensure `.dockerignore` excludes:

- `node_modules/`
- `target/`
- `.git/`

#### Solution 2: Clean directories

```bash
cd backend
mvn clean

cd ../frontend
rm -rf node_modules dist
```

### Issue 6: nginx cannot find files

**Error:**

```
nginx: [emerg] open() "/etc/nginx/conf.d/default.conf" failed
```

**Solutions:**

#### Solution 1: Verify nginx.conf exists

```bash
ls frontend/nginx.conf
```

#### Solution 2: Check Dockerfile COPY command

Ensure:

```dockerfile
COPY nginx.conf /etc/nginx/conf.d/default.conf
```

### Issue 7: Health check fails

**Error:**

```
Unhealthy
```

**Solutions:**

#### Solution 1: Check logs

```bash
docker-compose logs backend
docker-compose logs frontend
```

#### Solution 2: Increase health check timeout

In `docker-compose.yml`:

```yaml
healthcheck:
  start_period: 60s  # Increase from 40s
  interval: 30s
  timeout: 10s
  retries: 5
```

#### Solution 3: Test health endpoint manually

```bash
# Backend
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost/
```

## Quick Fixes

### Complete Clean Rebuild

```bash
# Stop everything
docker-compose down -v

# Remove all images
docker rmi $(docker images -q todo-*)

# Clean build cache
docker builder prune -a

# Rebuild from scratch
docker-compose up --build
```

### Build Individual Services

```bash
# Build backend only
docker-compose build backend

# Build frontend only
docker-compose build frontend

# Start specific service
docker-compose up backend
```

### View Detailed Logs

```bash
# All logs
docker-compose logs -f

# Backend only
docker-compose logs -f backend

# Frontend only
docker-compose logs -f frontend

# Last 100 lines
docker-compose logs --tail=100
```

### Interactive Debugging

```bash
# Access backend container
docker-compose exec backend sh

# Access frontend container
docker-compose exec frontend sh

# Check processes
docker-compose exec backend ps aux

# Check files
docker-compose exec backend ls -la /app
docker-compose exec frontend ls -la /usr/share/nginx/html
```

## Verification Steps

### 1. Check Docker Installation

```bash
docker --version
docker-compose --version
```

**Expected:**

- Docker: 20.10+
- Docker Compose: 2.0+

### 2. Verify Files Exist

```bash
# Check Dockerfiles
ls backend/Dockerfile
ls frontend/Dockerfile

# Check nginx config
ls frontend/nginx.conf

# Check docker-compose
ls docker-compose.yml

# Check .dockerignore
ls backend/.dockerignore
ls frontend/.dockerignore
```

### 3. Test Local Builds

```bash
# Test backend build
cd backend
mvn clean package -DskipTests
ls target/*.jar

# Test frontend build
cd ../frontend
npm install
npm run build
ls dist/
```

### 4. Verify Docker Compose

```bash
# Validate docker-compose.yml
docker-compose config

# Expected: No errors, shows parsed config
```

## Performance Optimization

### Use BuildKit

```bash
# Enable BuildKit (faster builds)
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

# Then build
docker-compose build
```

### Layer Caching

The Dockerfiles are already optimized with:

- Separate dependency installation (cached)
- Source code copy after dependencies
- Multi-stage builds (smaller images)

### Parallel Builds

```bash
# Build both services in parallel
docker-compose build --parallel
```

## Production Considerations

### Resource Limits

Add to `docker-compose.yml`:

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

### Restart Policies

Already configured:

```yaml
restart: unless-stopped
```

### Logging

Configure log rotation:

```yaml
services:
  backend:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## Testing the Setup

### Complete Test Sequence

```bash
# 1. Clean start
docker-compose down -v
docker system prune -a

# 2. Build
docker-compose build

# 3. Start
docker-compose up -d

# 4. Wait for services
sleep 30

# 5. Check health
docker-compose ps

# 6. Test backend
curl http://localhost:8080/actuator/health

# 7. Test frontend
curl http://localhost/

# 8. Test API through frontend
curl http://localhost/api/todos

# 9. View logs
docker-compose logs --tail=50

# 10. Success!
echo "✅ All services running"
```

## Getting Help

### Collect Debug Information

```bash
# System info
docker version
docker-compose version
uname -a

# Docker info
docker info

# Service status
docker-compose ps

# Recent logs
docker-compose logs --tail=200 > debug.log

# Inspect containers
docker-compose exec backend env
docker-compose exec frontend env
```

### Common Commands Reference

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart service
docker-compose restart backend

# View logs
docker-compose logs -f

# Execute command in container
docker-compose exec backend sh

# Remove volumes
docker-compose down -v

# Rebuild and restart
docker-compose up --build --force-recreate
```

## Summary

Most issues are resolved by:

1. ✅ Ensuring `package-lock.json` exists
2. ✅ Using the updated Dockerfiles
3. ✅ Cleaning Docker cache
4. ✅ Checking logs for specific errors
5. ✅ Verifying files exist and are correct

If problems persist, try a complete clean rebuild with:

```bash
docker-compose down -v && docker system prune -a && docker-compose up --build
```

---

**Still having issues?** Check the logs with `docker-compose logs -f` for specific error messages.
