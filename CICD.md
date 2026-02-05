# CI/CD Pipeline Documentation

## Overview

This project uses **GitHub Actions** for continuous integration and continuous deployment. The pipeline is split into
multiple workflows for better control and faster feedback.

## Workflows

### 1. CI - Pull Request (`ci-pr.yml`)

**Trigger:** Pull requests to `main` or `develop` branches

**Purpose:** Fast feedback on code changes before merging

**Jobs:**

#### Backend Build & Test

- Sets up JDK 21
- Caches Maven dependencies
- Runs unit tests with Surefire
- Duration: ~2-3 minutes
- Uploads test results

#### Backend Integration Tests

- Runs integration tests with Failsafe
- Tests with real H2 database
- Duration: ~1-2 minutes
- Uploads test results

#### Backend Code Coverage

- Generates JaCoCo coverage report
- Uploads to Codecov
- Comments coverage on PR
- Enforces 70% minimum coverage
- Duration: ~2-3 minutes

#### Frontend Build & Lint

- Sets up Node.js 20
- Installs dependencies
- Builds with Vite
- Duration: ~1-2 minutes
- Uploads build artifacts

#### Code Quality

- Runs SonarCloud analysis (optional)
- Provides code quality metrics
- Identifies code smells and bugs
- Duration: ~2-3 minutes

#### Security Scan

- Runs Trivy vulnerability scanner
- Performs OWASP dependency check
- Uploads results to GitHub Security
- Duration: ~2-3 minutes

#### PR Checks Summary

- Aggregates all job results
- Provides pass/fail status
- Required for PR approval

**Total Duration:** ~10-15 minutes

### 2. CD - Main Branch (`cd-main.yml`)

**Trigger:** Pushes to `main` branch or manual dispatch

**Purpose:** Build, test, package, and deploy the application

**Jobs:**

#### Test & Build

- Runs all tests (unit + integration)
- Generates coverage reports
- Builds JAR file
- Uploads artifacts
- Duration: ~3-4 minutes

#### Build Frontend

- Builds production frontend
- Uploads distribution files
- Duration: ~1-2 minutes

#### Docker Build & Push

- Builds multi-stage Docker images
- Pushes to GitHub Container Registry (ghcr.io)
- Tags: `main`, `main-<sha>`, `latest`
- Uses BuildKit cache for faster builds
- Duration: ~5-8 minutes

#### Security Scan

- Scans Docker images for vulnerabilities
- Uses Trivy scanner
- Uploads results to GitHub Security
- Duration: ~2-3 minutes

#### Create Release

- Extracts version from pom.xml
- Creates GitHub release
- Attaches JAR files
- Generates changelog
- Duration: ~1 minute

#### Deployment Notification

- Sends success/failure notifications
- Can integrate with Slack/Discord/Email

**Total Duration:** ~15-20 minutes

### 3. Dependency Updates (`dependencies.yml`)

**Trigger:** Weekly on Monday at 8 AM UTC or manual dispatch

**Purpose:** Check for outdated dependencies

**Jobs:**

#### Check Dependencies

- Checks Maven dependency updates
- Checks Maven plugin updates
- Checks npm package updates
- Uploads reports as artifacts
- Duration: ~2-3 minutes

### 4. Dependabot (`dependabot.yml`)

**Trigger:** Automatic weekly updates

**Purpose:** Automated dependency updates via PRs

**Configuration:**

- Maven dependencies (backend)
- npm dependencies (frontend)
- GitHub Actions versions
- Groups related dependencies
- Auto-assigns reviewers
- Adds appropriate labels

## Setup Instructions

### 1. Enable GitHub Actions

GitHub Actions are automatically enabled for public repositories. For private repositories:

1. Go to repository `Settings` → `Actions` → `General`
2. Select "Allow all actions and reusable workflows"
3. Click `Save`

### 2. Configure Secrets

Required secrets (Settings → Secrets and variables → Actions):

#### Optional Secrets

**`SONAR_TOKEN`** (for SonarCloud integration)

1. Go to https://sonarcloud.io
2. Create account and organization
3. Generate token: My Account → Security → Generate Token
4. Add to repository secrets

**`CODECOV_TOKEN`** (for private repositories)

1. Go to https://codecov.io
2. Add repository
3. Copy upload token
4. Add to repository secrets

**Note:** Codecov works without token for public repositories

### 3. Configure GitHub Container Registry

The pipeline automatically pushes Docker images to GitHub Container Registry (ghcr.io).

**Enable:**

1. Go to repository `Settings` → `Actions` → `General`
2. Under "Workflow permissions", select "Read and write permissions"
3. Check "Allow GitHub Actions to create and approve pull requests"
4. Click `Save`

**Images will be available at:**

- `ghcr.io/<username>/todo-app-backend:latest`
- `ghcr.io/<username>/todo-app-frontend:latest`

### 4. Branch Protection Rules

Recommended branch protection for `main`:

1. Go to `Settings` → `Branches`
2. Add rule for `main`
3. Enable:
    - ✅ Require a pull request before merging
    - ✅ Require approvals (1+)
    - ✅ Require status checks to pass before merging
        - Backend Build & Test
        - Backend Integration Tests
        - Backend Code Coverage
        - Frontend Build & Lint
    - ✅ Require conversation resolution before merging
    - ✅ Do not allow bypassing the above settings

## Workflow Files

### Directory Structure

```
.github/
├── workflows/
│   ├── ci-pr.yml           # PR checks
│   ├── cd-main.yml         # Main branch deployment
│   └── dependencies.yml    # Dependency updates
└── dependabot.yml          # Automated dependency updates
```

## Pipeline Stages

### Pull Request Flow

```
1. Developer creates PR
   ↓
2. CI Pipeline Triggered
   ├─ Backend Build & Test (parallel)
   ├─ Frontend Build (parallel)
   ├─ Security Scan (parallel)
   └─ Code Quality (parallel)
   ↓
3. Integration Tests Run
   ↓
4. Coverage Report Generated
   ↓
5. Coverage Comment Posted on PR
   ↓
6. All Checks Pass ✅
   ↓
7. PR Ready for Review
   ↓
8. Merge to Main
```

### Main Branch Flow

```
1. PR Merged to Main
   ↓
2. CD Pipeline Triggered
   ├─ All Tests Run
   ├─ Coverage Generated
   └─ JAR Built
   ↓
3. Docker Images Built
   ├─ Backend Image
   └─ Frontend Image
   ↓
4. Images Pushed to ghcr.io
   ↓
5. Security Scan on Images
   ↓
6. GitHub Release Created
   ↓
7. Notification Sent ✅
```

## Docker Images

### Backend Image

**Base:** Eclipse Temurin 21 JRE Alpine  
**Size:** ~200 MB  
**Features:**

- Multi-stage build
- Non-root user
- Health check
- Optimized JVM settings
- Container-aware resource limits

**Pull:**

```bash
docker pull ghcr.io/<username>/todo-app-backend:latest
```

**Run:**

```bash
docker run -p 8080:8080 ghcr.io/<username>/todo-app-backend:latest
```

### Frontend Image

**Base:** Nginx 1.25 Alpine  
**Size:** ~20 MB  
**Features:**

- Multi-stage build
- Optimized nginx config
- Gzip compression
- Security headers
- API proxy to backend
- Health check

**Pull:**

```bash
docker pull ghcr.io/<username>/todo-app-frontend:latest
```

**Run:**

```bash
docker run -p 80:80 ghcr.io/<username>/todo-app-frontend:latest
```

## Local Development with Docker

### Build & Run with Docker Compose

```bash
# Build and start
docker-compose up --build

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

**Access:**

- Frontend: http://localhost
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

### Individual Container Commands

```bash
# Build backend
cd backend
docker build -t todo-backend .

# Run backend
docker run -p 8080:8080 todo-backend

# Build frontend
cd frontend
docker build -t todo-frontend .

# Run frontend
docker run -p 80:80 todo-frontend
```

## Monitoring & Debugging

### View Workflow Runs

1. Go to repository → `Actions` tab
2. Select workflow from left sidebar
3. Click on specific run
4. View job details and logs

### Download Artifacts

Artifacts are available for 7-30 days:

- Test results
- Coverage reports
- Build artifacts
- Security scan results

**Download:**

1. Go to workflow run
2. Scroll to "Artifacts" section
3. Click artifact name to download

### Re-run Failed Workflows

1. Go to failed workflow run
2. Click "Re-run jobs" button
3. Select "Re-run failed jobs" or "Re-run all jobs"

## Coverage Reports

### Codecov Integration

Coverage reports are automatically uploaded to Codecov.

**View:**

- Go to https://codecov.io/gh/<username>/todo-app
- See coverage trends, file-level coverage, and more

**PR Comments:**
Coverage bot automatically comments on PRs with:

- Coverage change
- Files with coverage changes
- Link to full report

### Local Coverage

Generate locally:

```bash
cd backend
mvn clean verify
open target/site/jacoco/index.html
```

## Security Scanning

### Trivy Scanner

Scans for:

- OS vulnerabilities
- Language-specific vulnerabilities
- Security misconfigurations

**Results:** Uploaded to GitHub Security tab

### OWASP Dependency Check

Checks for:

- Known vulnerabilities in dependencies
- CVE database matches

**Results:** Available as workflow artifacts

### View Security Alerts

1. Go to repository → `Security` tab
2. Select `Code scanning` or `Dependabot alerts`
3. Review and fix vulnerabilities

## Performance Optimization

### Caching

The pipeline uses caching to speed up builds:

- Maven dependencies cache
- npm packages cache
- Docker layer cache (BuildKit)

**Average time savings:** 40-60%

### Parallel Execution

Jobs run in parallel when possible:

- Backend and frontend builds
- Security scans
- Code quality checks

### Matrix Strategy

Can be extended for multi-version testing:

```yaml
strategy:
  matrix:
    java: [ 21, 22 ]
    os: [ ubuntu-latest, windows-latest ]
```

## Troubleshooting

### Build Fails

**Check:**

1. View build logs in Actions tab
2. Check if dependencies changed
3. Verify Java/Node versions
4. Run locally to reproduce

### Tests Fail

**Check:**

1. View test results artifact
2. Check if new code broke tests
3. Run tests locally
4. Check for flaky tests

### Docker Build Fails

**Check:**

1. Verify Dockerfile syntax
2. Check if all files are copied
3. Verify base image is accessible
4. Build locally to debug

### Security Scan Fails

**Check:**

1. Review vulnerability details
2. Update vulnerable dependencies
3. Add exceptions if false positives
4. Check CVE database

## Best Practices

### ✅ Do

1. **Run tests locally** before pushing
2. **Keep PRs small** for faster CI
3. **Fix security vulnerabilities** immediately
4. **Monitor coverage trends**
5. **Review Dependabot PRs** regularly
6. **Use meaningful commit messages**
7. **Keep workflows up to date**

### ❌ Don't

1. **Don't skip tests** in CI
2. **Don't ignore security alerts**
3. **Don't merge failing PRs**
4. **Don't commit secrets**
5. **Don't disable required checks**

## Extending the Pipeline

### Add New Job

Edit workflow file:

```yaml
new-job:
  name: New Job
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    - name: Do something
      run: echo "Hello"
```

### Add Environment

For staging/production deployments:

```yaml
deploy-staging:
  environment:
    name: staging
    url: https://staging.example.com
```

### Add Notifications

Integrate with Slack:

```yaml
- name: Slack Notification
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

## Metrics & Analytics

### Key Metrics

Monitor these metrics:

- **Build success rate** - Should be >95%
- **Average build time** - Target <15 minutes
- **Test coverage** - Maintain >70%
- **Security vulnerabilities** - Keep at 0
- **Dependency freshness** - Update monthly

### GitHub Insights

View repository insights:

1. Go to `Insights` tab
2. Check `Pulse`, `Contributors`, `Traffic`
3. Monitor `Actions` usage

## Cost Optimization

### GitHub Actions Usage

- **Public repos:** Unlimited minutes ✅
- **Private repos:** 2,000 minutes/month (free tier)

### Reduce Usage

- Cache dependencies
- Run expensive jobs only on main
- Use self-hosted runners for private repos
- Optimize Docker builds

## Support & Resources

### Documentation

- GitHub Actions: https://docs.github.com/en/actions
- Docker: https://docs.docker.com
- Maven: https://maven.apache.org/guides/
- Vite: https://vitejs.dev/guide/

### Community

- GitHub Community: https://github.community
- Stack Overflow: Tag `github-actions`

## Summary

✅ **Complete CI/CD pipeline**

- Automated testing
- Code coverage reporting
- Security scanning
- Docker image building
- Automated releases

✅ **Best practices implemented**

- Multi-stage Docker builds
- Dependency caching
- Parallel execution
- Branch protection
- Automated dependency updates

✅ **Production-ready**

- Health checks
- Security hardening
- Performance optimization
- Monitoring integration

The pipeline is fully configured and ready to use! 🚀
