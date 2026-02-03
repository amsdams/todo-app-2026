# Surefire vs Failsafe Guide

## Overview

This project uses **two separate Maven plugins** for running tests:

1. **Maven Surefire** - Runs **unit tests** during `test` phase
2. **Maven Failsafe** - Runs **integration tests** during `integration-test` phase

## Why Two Plugins?

### The Problem with One Plugin

If you use only Surefire for all tests:

- ❌ Integration tests run during `mvn test`
- ❌ Build continues even if integration tests fail
- ❌ No separation between fast unit tests and slow integration tests
- ❌ Harder to run only unit tests in CI

### The Solution: Surefire + Failsafe

**Surefire** for unit tests:

- ✅ Fast feedback (runs during `test` phase)
- ✅ Fails build immediately if unit tests fail
- ✅ No infrastructure dependencies

**Failsafe** for integration tests:

- ✅ Runs after package is built
- ✅ Can start/stop test servers
- ✅ Separate from quick unit tests
- ✅ Better CI/CD pipeline control

## Test Naming Conventions

### Surefire (Unit Tests)

**Pattern:** `*Test.java`

**Examples:**

- `TodoTest.java` ✅
- `TodoServiceTest.java` ✅
- `TodoMapperTest.java` ✅

**Runs with:**

```bash
mvn test
```

### Failsafe (Integration Tests)

**Pattern:** `*IT.java` or `*IntegrationTest.java`

**Examples:**

- `TodoPersistenceAdapterIT.java` ✅
- `TodoControllerIT.java` ✅
- `DatabaseIntegrationTest.java` ✅

**Runs with:**

```bash
mvn verify
# or
mvn integration-test
```

## Configuration

### Surefire Configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
        <excludes>
            <exclude>**/*IT.java</exclude>
            <exclude>**/*IntegrationTest.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

**What it does:**

- ✅ Includes all `*Test.java` files
- ✅ Excludes `*IT.java` and `*IntegrationTest.java`
- ✅ Runs during `test` phase
- ✅ Fails build immediately on error

### Failsafe Configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.2.2</version>
    <executions>
        <execution>
            <id>integration-tests</id>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
            <include>**/*IntegrationTest.java</include>
        </includes>
    </configuration>
</plugin>
```

**What it does:**

- ✅ Includes only `*IT.java` and `*IntegrationTest.java`
- ✅ Runs during `integration-test` phase
- ✅ Verifies results during `verify` phase
- ✅ Allows cleanup even if tests fail

## Maven Lifecycle Phases

Understanding when each plugin runs:

```
validate → compile → test → package → integration-test → verify → install → deploy
                       ↑                        ↑           ↑
                    Surefire              Failsafe      Failsafe
                  (unit tests)         (run tests)   (verify results)
```

### Phase Details

1. **test** - Surefire runs unit tests
2. **package** - Creates JAR/WAR
3. **integration-test** - Failsafe runs integration tests
4. **verify** - Failsafe checks results and fails build if needed

## Commands Reference

### Run Only Unit Tests (Fast)

```bash
mvn test
```

**What runs:**

- TodoTest.java ✅
- TodoServiceTest.java ✅
- TodoControllerTest.java ✅

**What doesn't run:**

- TodoPersistenceAdapterIT.java ❌

**Duration:** ~5-10 seconds

### Run Unit + Integration Tests

```bash
mvn verify
```

**What runs:**

- All unit tests (via Surefire) ✅
- All integration tests (via Failsafe) ✅

**Duration:** ~15-20 seconds

### Run Only Integration Tests

```bash
mvn failsafe:integration-test
```

**What runs:**

- Only TodoPersistenceAdapterIT.java ✅

### Skip All Tests

```bash
mvn install -DskipTests
```

### Skip Only Unit Tests

```bash
mvn verify -Dsurefire.skip=true
```

### Skip Only Integration Tests

```bash
mvn verify -DskipITs
```

## Test Classification

### Unit Tests (Surefire)

**Characteristics:**

- ✅ Fast (milliseconds)
- ✅ No external dependencies
- ✅ Mock all dependencies
- ✅ Test single class/method
- ✅ Run frequently during development

**Examples in this project:**

- `TodoTest` - Tests domain model
- `TodoServiceTest` - Tests service with mocked repository
- `TodoControllerTest` - Tests controller with mocked service

**Naming:** `*Test.java`

### Integration Tests (Failsafe)

**Characteristics:**

- ⏱️ Slower (seconds)
- 🔌 Requires infrastructure (database, etc.)
- 🔗 Tests multiple components together
- 🏗️ Tests real interactions
- 📦 Run before release

**Examples in this project:**

- `TodoPersistenceAdapterIT` - Tests with real H2 database

**Naming:** `*IT.java` or `*IntegrationTest.java`

## Current Test Structure

```
src/test/java/
├── domain/
│   └── model/
│       └── TodoTest.java                    ← Surefire (unit)
├── application/
│   └── service/
│       └── TodoServiceTest.java             ← Surefire (unit)
└── infrastructure/
    ├── web/
    │   └── controller/
    │       └── TodoControllerTest.java      ← Surefire (unit)
    └── persistence/
        └── adapter/
            └── TodoPersistenceAdapterIT.java ← Failsafe (integration)
```

## Test Execution Report

After running `mvn verify`, you'll see:

```
[INFO] --- maven-surefire-plugin:3.2.2:test ---
[INFO] Running com.example.todo.domain.model.TodoTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.todo.application.service.TodoServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.todo.infrastructure.web.controller.TodoControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] --- maven-failsafe-plugin:3.2.2:integration-test ---
[INFO] Running com.example.todo.infrastructure.persistence.adapter.TodoPersistenceAdapterIT
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] --- maven-failsafe-plugin:3.2.2:verify ---
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

## Best Practices

### ✅ Do:

1. **Name tests correctly**
    - Unit tests: `*Test.java`
    - Integration tests: `*IT.java`

2. **Run unit tests frequently**
   ```bash
   mvn test  # Fast feedback
   ```

3. **Run integration tests before commit**
   ```bash
   mvn verify  # Complete validation
   ```

4. **Use appropriate annotations**
    - Unit tests: `@ExtendWith(MockitoExtension.class)`
    - Integration tests: `@SpringBootTest` or `@DataJpaTest`

5. **Mock in unit tests, use real components in integration tests**

### ❌ Don't:

1. **Don't mix test types**
    - Don't name integration tests `*Test.java`
    - Don't name unit tests `*IT.java`

2. **Don't use `@SpringBootTest` for unit tests**
    - Too slow
    - Defeats the purpose

3. **Don't skip integration tests in CI**
    - They catch real issues

## CI/CD Integration

### Fast Pipeline (Pull Requests)

```yaml
# Run only unit tests for quick feedback
- name: Unit Tests
  run: mvn test
```

**Duration:** ~10 seconds
**Purpose:** Fast feedback on code changes

### Complete Pipeline (Main Branch)

```yaml
# Run all tests
- name: All Tests
  run: mvn verify
```

**Duration:** ~20 seconds
**Purpose:** Complete validation before merge

### Nightly/Release Pipeline

```yaml
# Run all tests with coverage
- name: All Tests + Coverage
  run: mvn clean verify
  
- name: Upload Coverage
  uses: codecov/codecov-action@v3
```

**Duration:** ~25 seconds
**Purpose:** Full validation with coverage

## Coverage Integration

Both Surefire and Failsafe are integrated with JaCoCo:

```bash
# Coverage includes both unit and integration tests
mvn clean verify
open target/site/jacoco/index.html
```

JaCoCo automatically:

- ✅ Collects coverage from Surefire (unit tests)
- ✅ Collects coverage from Failsafe (integration tests)
- ✅ Combines coverage in single report
- ✅ Enforces 70% threshold across all tests

## Troubleshooting

### Issue: Integration tests run during `mvn test`

**Cause:** Test is named `*Test.java` instead of `*IT.java`

**Solution:**

```bash
# Rename file
mv MyIntegrationTest.java MyIT.java
```

### Issue: Integration tests not running

**Cause:** Test is named `*Test.java` so Surefire excludes it

**Solution:**

```bash
# Rename to *IT.java pattern
mv TodoPersistenceAdapterTest.java TodoPersistenceAdapterIT.java
```

### Issue: Build succeeds but integration tests failed

**Cause:** Not running `verify` goal

**Solution:**

```bash
# Use verify instead of install
mvn verify  # Not mvn install
```

### Issue: Tests run twice

**Cause:** Both plugins trying to run same test

**Solution:**
Check test naming:

- Unit tests: `*Test.java`
- Integration tests: `*IT.java`

## Performance Comparison

### Unit Tests Only (`mvn test`)

- Tests run: 22
- Duration: ~10 seconds
- Startup: Spring context NOT loaded
- Database: NOT started

### All Tests (`mvn verify`)

- Tests run: 28 (22 unit + 6 integration)
- Duration: ~20 seconds
- Startup: Spring context loaded for IT
- Database: H2 in-memory started

## Summary Table

| Aspect            | Surefire      | Failsafe             |
|-------------------|---------------|----------------------|
| **Purpose**       | Unit tests    | Integration tests    |
| **Pattern**       | `*Test.java`  | `*IT.java`           |
| **Phase**         | `test`        | `integration-test`   |
| **Command**       | `mvn test`    | `mvn verify`         |
| **Speed**         | Fast (ms)     | Slower (sec)         |
| **Dependencies**  | None (mocked) | Real (DB, etc.)      |
| **Fail behavior** | Immediate     | After cleanup        |
| **When to run**   | Always        | Before commit/deploy |

## Quick Commands Cheatsheet

```bash
# Development (fast feedback)
mvn test                    # Unit tests only

# Pre-commit (complete check)
mvn verify                  # Unit + Integration tests

# Skip all tests
mvn install -DskipTests     # Skip both

# Skip only unit tests
mvn verify -Dsurefire.skip  # Integration only

# Skip only integration tests
mvn verify -DskipITs        # Unit only

# With coverage
mvn clean verify            # All tests + coverage report
```

## Conclusion

✅ **Surefire** - Fast unit tests during development  
✅ **Failsafe** - Thorough integration tests before deployment  
✅ **Together** - Comprehensive test coverage at appropriate times

This separation provides:

- ⚡ Fast feedback during development
- 🔍 Thorough validation before deployment
- 🎯 Better CI/CD pipeline control
- 📊 Complete test coverage

---

**Best Practice:** Run `mvn test` frequently, `mvn verify` before commits!
