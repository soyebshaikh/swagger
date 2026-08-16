# Comprehensive Step-by-Step User Guide: Apex Banking App & Standalone Swagger Generator

This guide provides complete, step-by-step instructions on how to compile the **Apex Banking Application**, run the **Standalone Executable CLI JAR (`swagger-generator-cli.jar`)**, generate the **OpenAPI 3.1 Specification Reports** (`openapi.yaml`/`openapi.json`), launch the **Interactive Swagger UI**, and verify automatic API detection.

---

## Table of Contents
1. [Architectural Overview](#1-architectural-overview)
2. [Prerequisites](#2-prerequisites)
3. [Method 1: Using Standalone Executable CLI JAR (Zero Code Copying)](#method-1-using-standalone-executable-cli-jar-zero-code-copying)
4. [Method 2: One-Command Maven Execution](#method-2-one-command-maven-execution)
5. [Accessing Generated Swagger Reports & UI](#accessing-generated-swagger-reports--ui)
6. [Executing Sub-Commands & Unit Tests](#executing-sub-commands--unit-tests)
7. [Testing Automatic Detection for New APIs](#testing-automatic-detection-for-new-apis)
8. [Optional AI Documentation Enrichment](#optional-ai-documentation-enrichment)
9. [Clean Up / Deleting the Demo](#clean-up--deleting-the-demo)

---

## 1. Architectural Overview

The project is structured into two completely isolated modules:

```text
c:\Users\Soyeb\Desktop\swagger\
├── pom.xml                               # Root Maven POM (Banking App Backend)
├── src/                                  # Original Banking Application (Unmodified Spring XML 5.3.31)
│   ├── main/java/com/bank/
│   │   ├── model/                        # POJO Domain DTOs (User, Account, Loan, Payment, etc.)
│   │   ├── resource/                     # JAX-RS REST Endpoints (@Path, @GET, @POST, @Produces)
│   │   ├── service/                      # Unannotated Plain Java Domain Services
│   │   └── camel/                        # Apache Camel Integration Routes
│   └── main/resources/
│       ├── applicationContext.xml        # Master Spring XML Bean Definitions
│       ├── spring-mybatis.xml            # MyBatis Persistence Configuration
│       └── camel-context.xml             # Camel XML Routes
│
└── swagger-demo/                         # Isolated Standalone Swagger Generator Demo
    ├── pom.xml                           # Independent Demo Maven Build (with Shade Plugin)
    ├── target/
    │   └── swagger-generator-cli.jar    # Portable Fat Executable JAR File
    ├── generated/                        # Auto-generated openapi.yaml and openapi.json
    ├── docs/                             # Component Documentation
    └── src/
        ├── main/java/com/swagger/demo/   # AST Scanner, OpenAPI 3.1 Spec Generator, Server & Launcher
        └── test/java/com/swagger/demo/   # 17 Unit Tests (100% Pass)
```

### Zero-Impact Non-Invasive Guarantee:
- **Zero Code Copying**: With `swagger-generator-cli.jar`, you do not copy code into your target application.
- **Zero Annotations Added**: Source code files in `com.bank.*` contain zero `@Api` annotations.
- **Zero Spring Config Changes**: Main app remains pure Spring Framework 5.3.31 XML bean configuration.
- **No Port Conflicts**: Swagger UI runs on dedicated port `9090`.

---

## 2. Prerequisites

Ensure your development environment has:
- **Java Development Kit (JDK)**: Version 17 or higher (`java -version`).
- **Apache Maven**: Version 3.6 or higher (`mvn -version`).
- **Web Browser**: Chrome, Edge, Firefox, or Safari.

---

## Method 1: Using Standalone Executable CLI JAR (Zero Code Copying)

This is the recommended method when you want to generate Swagger documentation for **ANY Java project** without copying files or modifying `pom.xml`.

### Step 1: Build the Executable JAR
```bash
mvn clean package -DskipTests -f swagger-demo/pom.xml
```
*Creates `swagger-demo/target/swagger-generator-cli.jar`.*

### Step 2: Copy `swagger-generator-cli.jar` to Any Tools Folder
Copy `swagger-generator-cli.jar` to e.g. `C:\tools\swagger-generator-cli.jar`.

### Step 3: Run Against Any Java Project
Navigate to your project directory and execute:
```bash
java -jar C:\tools\swagger-generator-cli.jar
```
Or run from anywhere passing your project path:
```bash
java -jar C:\tools\swagger-generator-cli.jar --src C:\projects\your-java-app
```

---

## Method 2: One-Command Maven Execution

If you are working directly inside this project repository:

### Step 1: Build Main Banking Backend
```bash
mvn install -DskipTests
```

### Step 2: Run Generator & Launch Swagger UI
```bash
mvn compile exec:java -f swagger-demo/pom.xml
```

---

## Accessing Generated Swagger Reports & UI

### 1. Interactive Web UI
- **URL**: [`http://localhost:9090/swagger-ui/`](http://localhost:9090/swagger-ui/)
- *Features*: Displays interactive REST API documentation, endpoint tags, parameters, request body schemas, response payloads, and model definitions.

### 2. OpenAPI 3.1 YAML Specification Report
- **Local File Path**: [swagger-demo/generated/openapi.yaml](file:///c:/Users/Soyeb/Desktop/swagger/swagger-demo/generated/openapi.yaml)
- **Live HTTP URL**: [`http://localhost:9090/openapi.yaml`](http://localhost:9090/openapi.yaml)

### 3. OpenAPI 3.1 JSON Specification Report
- **Local File Path**: [swagger-demo/generated/openapi.json](file:///c:/Users/Soyeb/Desktop/swagger/swagger-demo/generated/openapi.json)
- **Live HTTP URL**: [`http://localhost:9090/openapi.json`](http://localhost:9090/openapi.json)

---

## Executing Sub-Commands & Unit Tests

### A. Run Static AST Controller Scanner Only (Console Printout)
```bash
mvn compile exec:java "-Dexec.mainClass=com.swagger.demo.ControllerScannerCommand" -f swagger-demo/pom.xml
```

### B. Run the Full Unit Test Suite (17 Tests)
```bash
mvn clean test -f swagger-demo/pom.xml
```

#### Test Suite Breakdown:
- `ModelScannerTest` (6 tests): Tests simple POJOs, nested models, `List<T>` collections, `BigDecimal` fields, and primitive vs wrapper nullability.
- `OpenApiGeneratorTest` (8 tests): Tests GET/POST endpoints, path variables (`{userId}`), query parameters (`accountNumber`), request/response bodies, `$ref` schema references, and syntax validation.
- `AiDocumentationServiceTest` (3 tests): Tests fallback safety and metadata merging.

---

## Testing Automatic Detection for New APIs

1. Add a new REST resource Java class under `src/main/java/com/bank/resource/MyNewResource.java` with `@Path` and HTTP annotations.
2. Recompile: `mvn install -DskipTests`
3. Run: `java -jar C:\tools\swagger-generator-cli.jar`
4. **Result**: The generator automatically detects the new endpoint, updates `openapi.yaml`, and updates the Swagger UI without requiring manually written Swagger annotations!

---

## Optional AI Documentation Enrichment

Set the `SWAGGER_AI_API_KEY` environment variable to enrich documentation with natural language summaries and category tags:

```bash
set SWAGGER_AI_API_KEY="your-api-key-here"
java -jar C:\tools\swagger-generator-cli.jar
```

*Note: If `SWAGGER_AI_API_KEY` is not set or API calls fail, the generator automatically uses sensible deterministic fallbacks without failing.*

---

## Clean Up / Deleting the Demo

Because the Swagger generator is 100% isolated inside the `swagger-demo/` directory or encapsulated in `swagger-generator-cli.jar`, it can be deleted at any time without impacting the core banking application:

```bash
rm -rf swagger-demo
```
