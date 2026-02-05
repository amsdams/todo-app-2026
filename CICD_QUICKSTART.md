# CI/CD Quick Start Guide

## 5-Minute Setup

### Step 1: Push to GitHub

```bash
# Initialize git (if not already)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit with CI/CD"

# Add remote (replace with your repository)
git remote add origin https://github.com/<username>/todo-app.git

# Push
git push -u origin main
```

### Step 2: GitHub Actions Runs Automatically ✅

Once pushed, GitHub Actions automatically:

- ✅ Runs all tests
- ✅ Generates coverage reports
- ✅ Builds Docker images
- ✅ Creates releases

**View:** Go to your repository → `Actions` tab

### Step 3: Enable Container Registry (Optional)

If you want to push Docker images:

1. Go to repository `Settings` → `Actions` → `General`
2. Under "Workflow permissions":
    - Select "Read and write permissions"
    - Check "Allow GitHub Actions to create and approve pull requests"
3. Click `Save`

### Step 4: Set Up Branch Protection (Recommended)

1. Go to `Settings` → `Branches`
2. Click "Add rule"
3. Branch name pattern: `main`
4. Enable:
    - ✅ Require a pull request before merging
    - ✅ Require status checks to pass
    - Select: Backend Build & Test, Frontend Build
5. Click "Create"

## That's It! 🎉

Your CI/CD pipeline is now active!

## What Happens Next

### On Pull Requests

```
1. Create branch
   git checkout -b feature/my-feature

2. Make changes and commit
   git add .
   git commit -m "Add feature"

3. Push branch
   git push origin feature/my-feature

4. Create PR on GitHub
   → CI runs automatically
   → Tests, coverage, security checks
   → Results shown in PR

5. After approval, merge
   → CD runs automatically
   → Builds Docker images
   → Creates release
```

### View Results

**CI Results (PR):**

- Go to PR → "Checks" tab
- See all test results
- View coverage report

**CD Results (Main):**

- Go to "Actions" tab
- Click "CD - Main Branch"
- See Docker build and release

**Docker Images:**

- Go to repository main page
- Click "Packages" (right sidebar)
- See published images

## Testing the Pipeline

### Test CI (without creating PR)

```bash
# Manually trigger workflow
gh workflow run ci-pr.yml

# Or via GitHub UI
# Actions → CI - Pull Request → Run workflow
```

### Test CD

```bash
# Push to main (or merge PR)
git push origin main

# Or manually trigger
gh workflow run cd-main.yml
```

## Pull Docker Images

After CD runs:

```bash
# Backend
docker pull ghcr.io/<username>/todo-app-backend:latest

# Frontend
docker pull ghcr.io/<username>/todo-app-frontend:latest

# Run with docker-compose
docker-compose up -d
```

## Common Issues

### Issue: Actions not running

**Solution:**

- Go to repository `Settings` → `Actions` → `General`
- Ensure "Allow all actions and reusable workflows" is selected

### Issue: Docker push fails

**Solution:**

- Enable workflow permissions (see Step 3 above)
- Ensure you're pushing to main branch

### Issue: Tests fail

**Solution:**

- Check test output in Actions tab
- Run tests locally: `mvn verify`
- Fix failing tests and push again

## Optional Integrations

### Add Codecov (for coverage badges)

1. Go to https://codecov.io
2. Sign in with GitHub
3. Add repository
4. Copy token (optional for public repos)
5. Add `CODECOV_TOKEN` secret if needed

### Add SonarCloud (for code quality)

1. Go to https://sonarcloud.io
2. Sign in with GitHub
3. Create organization
4. Add repository
5. Copy token
6. Add `SONAR_TOKEN` to repository secrets

## Next Steps

- [ ] Customize workflows if needed
- [ ] Set up staging environment
- [ ] Configure deployment to cloud
- [ ] Add notification integrations (Slack, Discord)
- [ ] Set up monitoring (optional)

## Resources

- [Full CI/CD Documentation](CICD.md)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Docker Docs](https://docs.docker.com)

---

**Everything is configured and ready to go!** 🚀

Just push your code and watch the magic happen ✨
