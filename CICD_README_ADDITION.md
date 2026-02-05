# CI/CD Status & Docker

## Build Status

[![CI - Pull Request](https://github.com/<username>/todo-app/actions/workflows/ci-pr.yml/badge.svg)](https://github.com/<username>/todo-app/actions/workflows/ci-pr.yml)
[![CD - Main Branch](https://github.com/<username>/todo-app/actions/workflows/cd-main.yml/badge.svg)](https://github.com/<username>/todo-app/actions/workflows/cd-main.yml)
[![codecov](https://codecov.io/gh/<username>/todo-app/branch/main/graph/badge.svg)](https://codecov.io/gh/<username>/todo-app)

**Note:** Replace `<username>` with your GitHub username

## CI/CD Pipeline

This project uses **GitHub Actions** for continuous integration and deployment:

### Continuous Integration (PR)

- ✅ Automated unit tests
- ✅ Integration tests
- ✅ Code coverage (70% minimum)
- ✅ Security scanning
- ✅ Code quality checks
- ⚡ Fast feedback (~10-15 minutes)

### Continuous Deployment (Main)

- 🚀 Docker image builds
- 📦 GitHub Container Registry
- 🔍 Security scanning
- 📋 Automated releases
- 📊 Coverage reports

See [CICD.md](CICD.md) for complete documentation.

## Docker Images

### Pull Images

```bash
# Backend
docker pull ghcr.io/<username>/todo-app-backend:latest

# Frontend
docker pull ghcr.io/<username>/todo-app-frontend:latest
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

**Access:**

- Frontend: http://localhost
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### Individual Containers

```bash
# Run backend
docker run -p 8080:8080 ghcr.io/<username>/todo-app-backend:latest

# Run frontend
docker run -p 80:80 ghcr.io/<username>/todo-app-frontend:latest
```

## Workflows

1. **CI - Pull Request** ([`ci-pr.yml`](.github/workflows/ci-pr.yml))
    - Runs on every PR
    - Fast feedback for developers
    - Required checks for merge

2. **CD - Main Branch** ([`cd-main.yml`](.github/workflows/cd-main.yml))
    - Runs on merge to main
    - Builds and pushes Docker images
    - Creates GitHub releases

3. **Dependency Updates** ([`dependencies.yml`](.github/workflows/dependencies.yml))
    - Weekly dependency checks
    - Automated via Dependabot

