# Self Versioning Kotlin Lib

A template for Kotlin libraries that automatically manage their SemVer versioning and publish to GitHub Packages. This library provides utilities for concurrent processing with Kotlin coroutines.

## Overview

This project serves two main purposes:

1. **Self-Versioning Template**: A ready-to-use template for Kotlin libraries that automatically manage their semantic versioning based on commit messages.
2. **Utility Library**: Provides useful utilities for concurrent processing with Kotlin coroutines, including a worker pool implementation and delay utilities.

## Features

- Automatic semantic versioning based on commit messages
- GitHub Packages integration for easy distribution
- Worker pool implementation for parallel processing
- Delay utilities for coroutine-based code

## Installation

### Gradle (Kotlin DSL)

Add the GitHub Packages repository to your `settings.gradle.kts` or `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/renatomrcosta/self-versioning-kt-lib")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.xunfos:self-versioning-kt-lib:1.0.0") // Replace with the latest version
}
```

### Maven

Add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/renatomrcosta/self-versioning-kt-lib</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

Then add the dependency:

```xml
<dependency>
    <groupId>com.xunfos</groupId>
    <artifactId>self-versioning-kt-lib</artifactId>
    <version>1.0.0</version> <!-- Replace with the latest version -->
</dependency>
```

## Usage

### Worker Pool

The `WorkerPool` class provides a way to process items in parallel using a pool of workers:

```kotlin
fun main() = runBlocking {
    // Create a worker pool that processes strings and returns integers
    val workerPool = WorkerPool<String, Int>()

    // Launch 5 workers that convert strings to their lengths
    workerPool.launchWorkers(5) { worker ->
        println("Worker ${worker.id} processing: ${worker.value}")
        worker.value.length
    }

    // Submit work items
    workerPool.doWork("Hello")
    workerPool.doWork("World")
    workerPool.doWork("Kotlin")

    // Collect results
    workerPool.output.collect { result ->
        println("Result: $result")
    }
}
```

### Delay Utility

The `withDelay` function allows you to add a delay before executing a block of code:

```kotlin
fun main() = runBlocking {
    // Execute a block after a 500ms delay
    val result = withDelay(500) {
        println("Executing after delay")
        "Result"
    }

    println("Got result: $result")
}
```

## API Documentation

### WorkerPool

```kotlin
class WorkerPool<T, U>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
)
```

A class that manages a pool of workers for parallel processing.

**Properties:**
- `output: Flow<U>` - A flow of results produced by the workers.

**Methods:**
- `suspend fun doWork(input: T)` - Submits an item for processing.
- `suspend fun launchWorkers(amountOfWorkers: Int, work: suspend (Worker<T>) -> U)` - Launches a specified number of workers that process items using the provided work function.

### Worker

```kotlin
data class Worker<T>(val id: Int, val value: T)
```

Represents a worker with an ID and a value to process.

### withDelay

```kotlin
suspend fun <T> withDelay(timeoutMillis: Long = 100L, block: () -> T): T
```

Executes the provided block after a specified delay.

**Parameters:**
- `timeoutMillis: Long` - The delay in milliseconds (default: 100ms).
- `block: () -> T` - The block to execute after the delay.

**Returns:**
- The result of the block execution.

## Setting Up Your Own Self-Versioning Library

### Prerequisites

- GitHub repository
- Basic knowledge of Gradle and GitHub Actions

### Setup Steps

1. **Create a GitHub Secret**:
   Ensure your repository has a secret named `PACKAGE_RELEASE_TOKEN` with permissions to release packages (`write:packages`).

2. **Follow Commit Message Convention**:
   Commits must follow the [Angular Commit Message format](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#-commit-message-format).

3. **Understand the Workflow**:
   - The `semantic_release.yml` workflow runs on pushes to main, running tests and the semantic release plugin.
   - The `publish.yml` workflow triggers on release creation, publishing the library to GitHub Packages.

## How It Works

The self-versioning mechanism uses the [semantic-release](https://github.com/semantic-release/semantic-release) npm package to:

1. Analyze commit messages since the last tag
2. Determine which version component (MAJOR, MINOR, or PATCH) should be incremented
3. Create a new GitHub release with the appropriate version

The GitHub Actions workflows handle:

1. **Semantic Release**: Triggered on pushes to main, creates a new release if needed.
2. **Publishing**: Triggered on release creation, publishes the library to GitHub Packages.

## Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes following the Angular commit format
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Sample Usage

A sample of usage of this lib can be seen at [this commit](https://github.com/renatomrcosta/boot3-playground/commit/6569be221b157bd4256a82eb97414f59eec7e5cd).
