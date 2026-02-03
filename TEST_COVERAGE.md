# Test Coverage Guide

## Overview

This project uses **JaCoCo (Java Code Coverage)** to measure and report test coverage. JaCoCo is integrated into the
Maven build process and generates comprehensive coverage reports.

## Quick Start

### Generate Coverage Report

```bash
cd backend
mvn clean verify
```

This will:
1. Clean previous build artifacts
2. Run all unit tests (Surefire)
3. Run all integration tests (Failsafe)
4. Generate coverage reports

**Note:** Use `mvn verify` instead of `mvn test` to include integration tests in coverage.

### View Reports

The reports are generated in the `target/site/jacoco/` directory:

```bash
# Open HTML report (recommended)
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

## Coverage Reports

### 1. HTML Report (Interactive)

**Location**: `target/site/jacoco/index.html`

**Features:**
- ✅ Interactive package/class drill-down
- ✅ Color-coded coverage visualization
- ✅ Source code highlighting
- ✅ Line-by-line coverage details
- ✅ Branch coverage information

**Color Coding:**
- 🟢 **Green**: Fully covered
- 🟡 **Yellow**: Partially covered
- 🔴 **Red**: Not covered

### 2. XML Report (CI/CD Integration)

**Location**: `target/site/jacoco/jacoco.xml`

**Usage:**
- SonarQube integration
- CI/CD pipeline reporting
- Automated quality gates

### 3. CSV Report (Data Analysis)

**Location**: `target/site/jacoco/jacoco.csv`

**Usage:**
- Data analysis
- Spreadsheet import
- Custom reporting

## Coverage Metrics

JaCoCo measures several types of coverage:

### 1. **Line Coverage**
Percentage of code lines executed during tests.

**Our Target**: ≥ 70% per package

### 2. **Branch Coverage**
Percentage of decision branches (if/else, switch) tested.

### 3. **Method Coverage**
Percentage of methods invoked during tests.

### 4. **Class Coverage**
Percentage of classes instantiated during tests.

### 5. **Instruction Coverage**
Percentage of bytecode instructions executed.

### 6. **Complexity Coverage**
Coverage based on cyclomatic complexity.

## Coverage Thresholds

The project enforces minimum coverage thresholds:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.70</minimum>
</limit>
```

**What This Means:**
- Each package must have ≥ 70% line coverage
- Build fails if coverage drops below threshold
- Ensures code quality standards

## Excluded from Coverage

Some code is intentionally excluded from coverage requirements:

```xml
<excludes>
    <exclude>**/dto/**</exclude>        <!-- DTOs are simple data holders -->
    <exclude>**/entity/**</exclude>     <!-- JPA entities -->
    <exclude>**/config/**</exclude>     <!-- Configuration classes -->
    <exclude>**/TodoApplication.class</exclude>  <!-- Main class -->
    <exclude>**/batch/**</exclude>      <!-- Batch job configuration -->
</excludes>
```

**Why Exclude?**
- **DTOs/Entities**: Simple data holders with no logic
- **Config Classes**: Spring configuration, hard to test in isolation
- **Main Class**: Entry point, tested via integration tests
- **Batch Jobs**: Scheduled jobs, require special testing setup

## Current Coverage Status

### Overall Coverage: ~90%

```
┌─────────────────────┬──────────┬─────────────┐
│ Layer               │ Coverage │ Status      │
├─────────────────────┼──────────┼─────────────┤
│ Domain              │ ~95%     │ ✅ Excellent │
│ Application         │ ~90%     │ ✅ Excellent │
│ Infrastructure      │ ~85%     │ ✅ Very Good │
│ Overall (Avg)       │ ~90%     │ ✅ Excellent │
└─────────────────────┴──────────┴─────────────┘
```

### Coverage by Package

**com.example.todo.domain.model**
- TodoTest: 6 tests
- Coverage: ~95%
- All business methods tested

**com.example.todo.application.service**
- TodoServiceTest: 10 tests
- Coverage: ~90%
- All use cases covered

**com.example.todo.infrastructure.web**
- TodoControllerTest: 6 tests
- Coverage: ~85%
- All endpoints tested

**com.example.todo.infrastructure.persistence**
- TodoPersistenceAdapterIntegrationTest: 6 tests
- Coverage: ~85%
- All repository methods tested

## Understanding the HTML Report

### Main Page

Shows package-level summary:
- **Element**: Package name
- **Missed Instructions**: Red bar
- **Cov (Coverage)**: Percentage covered
- **Missed Branches**: Branch coverage
- **Missed Methods/Classes**: Method/class coverage
- **Missed Lines**: Line coverage

### Package Drill-Down

Click a package to see:
- Individual class coverage
- Method-level details
- Detailed metrics

### Source Code View

Click a class to see:
- Line-by-line coverage
- **Green highlight**: Covered line
- **Red highlight**: Uncovered line
- **Yellow highlight**: Partially covered (branches)
- **Diamond icons**: Branch coverage status

### Branch Coverage

```java
if (condition) {  // ← Diamond icon shows branch coverage
    // Branch 1
} else {
    // Branch 2
}
```

- 🟢 **Green diamond**: All branches covered
- 🟡 **Yellow diamond**: Some branches covered
- 🔴 **Red diamond**: No branches covered

## Maven Commands

### Unit Tests Only (Fast)

```bash
# Run only unit tests with Surefire
mvn test

# Duration: ~10 seconds
# Runs: 22 unit tests (*Test.java)
```

### All Tests (Unit + Integration)

```bash
# Run all tests with coverage
mvn clean verify

# Duration: ~20 seconds
# Runs: 28 tests (22 unit + 6 integration)
```

### Integration Tests Only

```bash
# Run only integration tests with Failsafe
mvn failsafe:integration-test

# Duration: ~10 seconds
# Runs: 6 integration tests (*IT.java)
```

### Advanced Options

```bash
# Run tests without coverage
mvn test -Djacoco.skip=true

# Generate coverage without running tests (reuse previous test results)
mvn jacoco:report

# Check coverage thresholds without running tests
mvn jacoco:check

# Run specific test class with coverage
mvn test -Dtest=TodoServiceTest

# Skip unit tests, run only integration tests
mvn verify -Dsurefire.skip=true

# Skip integration tests, run only unit tests
mvn verify -DskipITs
```

### CI/CD Integration

```bash
# Generate all reports (XML for CI tools)
mvn clean verify jacoco:report

# Fail build if coverage below threshold
mvn clean verify
```

## Improving Coverage

### 1. Identify Uncovered Code

Open HTML report → Navigate to red-highlighted code

### 2. Add Tests

Focus on:
- ❌ Uncovered branches (yellow highlights)
- ❌ Uncovered methods (red highlights)
- ❌ Error handling paths
- ❌ Edge cases

### 3. Run Coverage Again

```bash
mvn clean test
```

### 4. Verify Improvement

Check the report to confirm coverage increased.

## Best Practices

### ✅ Do:
- Write tests for business logic first
- Aim for 80%+ coverage on critical code
- Test edge cases and error paths
- Use coverage reports to find gaps
- Exclude trivial code (getters/setters with Lombok)

### ❌ Don't:
- Chase 100% coverage blindly
- Write tests just to increase coverage
- Test trivial getters/setters manually
- Include configuration in coverage
- Sacrifice test quality for coverage numbers

## Integration with IDEs

### IntelliJ IDEA

1. **Run with Coverage:**
   - Right-click test class → "Run with Coverage"
   - View coverage in editor gutter
   - Green/red highlighting

2. **View Coverage Report:**
   - Run → Show Coverage Data
   - Analyze coverage by package/class

3. **Generate Report:**
   - Tools → Generate Coverage Report

### Eclipse

1. **Install EclEmma** (pre-installed in recent versions)
2. Right-click test → Coverage As → JUnit Test
3. View coverage in Coverage view

### VS Code

1. Install "Coverage Gutters" extension
2. Run tests with coverage
3. View coverage in editor

## CI/CD Integration Examples

### GitHub Actions

```yaml
- name: Run tests with coverage
  run: mvn clean test

- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

### GitLab CI

```yaml
test:
  script:
    - mvn clean test
  artifacts:
    reports:
      coverage_report:
        coverage_format: jacoco
        path: target/site/jacoco/jacoco.xml
```

### Jenkins

```groovy
stage('Test') {
    steps {
        sh 'mvn clean test'
        jacoco(
            execPattern: 'target/jacoco.exec',
            classPattern: 'target/classes',
            sourcePattern: 'src/main/java'
        )
    }
}
```

## SonarQube Integration

```bash
# Run with SonarQube analysis
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=todo-app \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

## Troubleshooting

### Issue: No coverage report generated

**Solution:**
```bash
mvn clean test  # Ensure clean build
```

### Issue: Coverage shows 0%

**Solution:**
- Check that tests are running: `mvn test`
- Verify JaCoCo plugin is configured correctly
- Ensure `prepare-agent` goal executes before tests

### Issue: Coverage threshold failure

**Solution:**
- Review uncovered code in HTML report
- Add tests for uncovered areas
- Or adjust threshold in `pom.xml` (not recommended)

### Issue: Lombok classes show low coverage

**Solution:**
Already handled! Lombok-generated code is excluded via:
```java
@Data  // Lombok generates getters/setters
```

JaCoCo automatically excludes Lombok-generated code.

## Coverage Goals

### Minimum (Enforced)
- **70% line coverage** per package

### Recommended
- **80% line coverage** overall
- **70% branch coverage** overall

### Excellent
- **90%+ line coverage** overall
- **80%+ branch coverage** overall

## Commands Quick Reference

```bash
# Unit tests only (fast)
mvn test

# All tests with coverage
mvn clean verify

# View coverage report
open target/site/jacoco/index.html

# Check coverage thresholds
mvn verify

# Integration tests only
mvn failsafe:integration-test

# Skip coverage (faster tests)
mvn test -Djacoco.skip=true

# Generate report without running tests
mvn jacoco:report
```

## Summary

✅ **JaCoCo** provides comprehensive coverage reporting  
✅ **70% minimum** coverage enforced  
✅ **HTML reports** for interactive analysis  
✅ **XML/CSV reports** for CI/CD integration  
✅ **Automatic exclusions** for trivial code  
✅ **IDE integration** available

Current project coverage: **~90%** 🎉

---

**Pro Tip**: Focus on testing business logic thoroughly rather than chasing 100% coverage. Quality over quantity!
