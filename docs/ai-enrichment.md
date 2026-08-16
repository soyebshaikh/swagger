# Optional AI Documentation Enrichment

This document describes the design, environment configuration, and fallback resilience of the optional **AI Documentation Enrichment Service** in the `swagger-demo` module.

---

## 1. Strict Structural vs. Non-Structural Boundaries

To guarantee 100% deterministic accuracy and code alignment, structural elements and API contracts are strictly generated via static Java AST source code analysis. AI is **NEVER** permitted to infer structural code contracts.

| Category | Component | Source of Truth |
| :--- | :--- | :--- |
| **Structural** | HTTP Verbs (`GET`, `POST`, `PUT`, `DELETE`) | **Deterministic Java AST Analysis** |
| **Structural** | URL Paths (`/accounts/balance`, `/loans/user/{userId}`) | **Deterministic Java AST Analysis** |
| **Structural** | Request & Response Payload DTO Types | **Deterministic Java AST Analysis** |
| **Structural** | Java Data Types & Formats (`BigDecimal`, `int64`, etc.) | **Deterministic Java AST Analysis** |
| **Non-Structural** | API Summaries & Detailed Descriptions | **Optional AI Enrichment** |
| **Non-Structural** | OpenAPI Category Tags | **Optional AI Enrichment** |
| **Non-Structural** | Parameter Explanations & Field Descriptions | **Optional AI Enrichment** |
| **Non-Structural** | Sample Values | **Optional AI Enrichment** |

---

## 2. Environmental Configuration (`SWAGGER_AI_API_KEY`)

The AI enrichment service reads the API key exclusively from the environment variable:

```bash
export SWAGGER_AI_API_KEY="your-secret-api-key"
```

### Security Rules:
- API keys are **NEVER** hardcoded in Java source files or Maven `pom.xml`.
- If `SWAGGER_AI_API_KEY` is not present, the generator logs a lightweight message and uses sensible fallback descriptions.

---

## 3. Fallback & Fault Tolerance Architecture

```text
Check System.getenv("SWAGGER_AI_API_KEY")
                │
     ┌──────────┴──────────┐
     ▼                     ▼
(API Key Present)     (API Key Missing / HTTP Error / Timeout)
Call AI REST Service   Return Deterministic Fallback Metadata:
                       • Summary: "Endpoint: <methodName>"
                       • Description: "Discovered automatically from method..."
                       • Tags: ["<ControllerClassName>"]
     │                     │
     └──────────┬──────────┘
                ▼
Merge Metadata into OpenAPI 3.1 Spec (openapi.yaml)
```

---

## 4. Test Verification Suite (`AiDocumentationServiceTest`)

JUnit 5 unit tests in `com.swagger.demo.AiDocumentationServiceTest`:

1. **`testAiServiceDisabledFallback`**: Verifies that when `SWAGGER_AI_API_KEY` is absent, the generator creates valid OpenAPI specs using fallback descriptions without throwing exceptions or failing.
2. **`testAiServiceEnabled`**: Verifies generating natural language summaries and tags when the API key is present.
3. **`testMergeAiMetadataIntoOpenApi`**: Verifies merging AI summaries, descriptions, and tags into `Operation`, `Parameter`, and `Schema` properties in `openapi.yaml`.

---

## 5. How to Run with AI Enrichment Enabled

You can enable optional AI documentation enrichment with either execution method:

### Method A: Using Standalone Executable CLI JAR (Recommended)

#### Windows (PowerShell):
```powershell
$env:SWAGGER_AI_API_KEY="your-secret-api-key"
java -jar C:\tools\swagger-generator-cli.jar --src C:\projects\your-java-project
```

#### Linux / macOS (Bash):
```bash
export SWAGGER_AI_API_KEY="your-secret-api-key"
java -jar /usr/local/bin/swagger-generator-cli.jar --src /projects/your-java-project
```

---

### Method B: Using One-Command Maven Execution

#### Windows (PowerShell):
```powershell
$env:SWAGGER_AI_API_KEY="your-secret-api-key"
mvn compile exec:java -f swagger-demo/pom.xml
```

#### Linux / macOS (Bash):
```bash
export SWAGGER_AI_API_KEY="your-secret-api-key"
mvn compile exec:java -f swagger-demo/pom.xml
```
