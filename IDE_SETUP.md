# IDE Setup Guide

## Lombok Configuration

This project uses Lombok to reduce boilerplate code. You need to enable annotation processing in your IDE.

### IntelliJ IDEA

1. **Install Lombok Plugin:**
   - Go to `File` → `Settings` (Windows/Linux) or `IntelliJ IDEA` → `Preferences` (Mac)
   - Navigate to `Plugins`
   - Search for "Lombok"
   - Click `Install` and restart IntelliJ

2. **Enable Annotation Processing:**
   - Go to `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
   - Check ✅ `Enable annotation processing`
   - Click `Apply` and `OK`

3. **Rebuild Project:**
   - Go to `Build` → `Rebuild Project`
   - Or run: `mvn clean install`

### Eclipse

1. **Install Lombok:**
   - Download `lombok.jar` from https://projectlombok.org/download
   - Double-click the jar file
   - Select your Eclipse installation
   - Click `Install/Update`
   - Restart Eclipse

2. **Verify Installation:**
   - Check `About Eclipse` → it should mention Lombok

3. **Rebuild Project:**
   - Right-click project → `Maven` → `Update Project`
   - Check `Force Update of Snapshots/Releases`

### VS Code

1. **Install Extensions:**
   - Install "Language Support for Java(TM) by Red Hat"
   - Install "Lombok Annotations Support for VS Code"

2. **Enable Annotation Processing:**
   - Open `settings.json`
   - Add:
   ```json
   {
     "java.configuration.updateBuildConfiguration": "automatic",
     "java.compile.nullAnalysis.mode": "automatic"
   }
   ```

3. **Rebuild:**
   - Run: `mvn clean install`

## Troubleshooting

### "Cannot find symbol" errors for getters/setters

This means Lombok annotation processing is not enabled.

**Solution:**
1. Make sure Lombok plugin is installed
2. Enable annotation processing (see above)
3. Clean and rebuild:
   ```bash
   mvn clean install
   ```

### Tests not running in IDE

**Solution:**
1. Right-click on `pom.xml`
2. Select `Maven` → `Reload Project` (IntelliJ)
3. Or `Maven` → `Update Project` (Eclipse)

### Deprecated `@MockBean` warnings

This is expected in Spring Boot 3.4+. The warnings don't affect functionality. We've updated the code to use `@TestConfiguration` with manual mocking to avoid these warnings.

## Running from Command Line

If you prefer to avoid IDE issues, you can always run from the command line:

```bash
# Clean build
mvn clean install

# Run tests only
mvn test

# Run application
mvn spring-boot:run
```

## Verification

After setup, verify Lombok is working:

```bash
cd backend
mvn clean test
```

All tests should pass:
- ✅ TodoTest (6 tests)
- ✅ TodoServiceTest (10 tests)  
- ✅ TodoControllerTest (6 tests)
- ✅ TodoPersistenceAdapterIntegrationTest (6 tests)

**Total: 28 tests should pass**

## Common Issues

### Issue: Getters/Setters not found
**Cause:** Annotation processing disabled
**Fix:** Enable it in IDE settings (see above)

### Issue: Cannot resolve Lombok annotations
**Cause:** Lombok plugin not installed
**Fix:** Install plugin and restart IDE

### Issue: Tests fail with "ApplicationContext failure"
**Cause:** Batch configuration trying to load
**Fix:** Already handled in test configuration

## Clean Build

If you encounter persistent issues:

```bash
# Delete all build artifacts
mvn clean

# Delete IDE-specific files
rm -rf .idea/
rm -rf target/
rm *.iml

# Rebuild
mvn clean install

# Reimport in IDE
# File → Invalidate Caches / Restart (IntelliJ)
```

## Success Indicators

✅ No compilation errors  
✅ All 28 tests pass  
✅ Application starts successfully  
✅ Swagger UI loads at http://localhost:8080/swagger-ui.html  
✅ No deprecation warnings  

If all indicators are green, you're good to go! 🎉
