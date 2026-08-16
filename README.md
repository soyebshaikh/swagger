# Apex Banking Backend Application & Standalone Swagger Generator

A Spring Framework 5.3.31 Banking Application Backend (Pure Spring XML Bean Configuration + MyBatis + Apache Camel + JAX-RS REST API) equipped with a **100% Isolated, Zero-Impact Standalone Swagger/OpenAPI 3.1 Spec Generator & Interactive UI**.

---

## 🚀 Quick Start Guide

You can run the Swagger Generator using **EITHER** a standalone executable JAR (Zero code copying required) **OR** Maven execution.

### Method 1: Portable Standalone Executable CLI JAR (Recommended - Zero Code Copying)

Build or locate `swagger-generator-cli.jar` at `swagger-demo/target/swagger-generator-cli.jar`. Copy it to any tools folder (e.g. `C:\tools\swagger-generator-cli.jar`).

Run against **ANY** Java project on your system without modifying code or `pom.xml`:

```bash
java -jar C:\tools\swagger-generator-cli.jar --src C:\path\to\your-java-project
```
or navigate into your project directory and run:
```bash
java -jar C:\tools\swagger-generator-cli.jar
```

---

### Method 2: One-Command Maven Execution

If working inside this repository, run:

1. **Build Main Application Backend**:
   ```bash
   mvn install -DskipTests
   ```
2. **Run Generator & Launch Swagger UI**:
   ```bash
   mvn compile exec:java -f swagger-demo/pom.xml
   ```

---

## 🌐 Generated Spec Reports & Swagger UI

- **Interactive Swagger UI**: [`http://localhost:9090/swagger-ui/`](http://localhost:9090/swagger-ui/)
- **OpenAPI 3.1 YAML Spec**: [`swagger-demo/generated/openapi.yaml`](file:///c:/Users/Soyeb/Desktop/swagger/swagger-demo/generated/openapi.yaml) or `http://localhost:9090/openapi.yaml`
- **OpenAPI 3.1 JSON Spec**: [`swagger-demo/generated/openapi.json`](file:///c:/Users/Soyeb/Desktop/swagger/swagger-demo/generated/openapi.json) or `http://localhost:9090/openapi.json`

---

## 📚 Complete Project Documentation Index

- **[docs/USER_GUIDE.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/USER_GUIDE.md)** - Complete step-by-step user guide
- **[docs/cli-tool-guide.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/cli-tool-guide.md)** - Standalone Executable CLI JAR guide
- **[docs/run-demo.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/run-demo.md)** - One-command developer launcher
- **[docs/controller-scanner.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/controller-scanner.md)** - Static AST controller scanner
- **[docs/model-scanner.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/model-scanner.md)** - Static AST model scanner
- **[docs/openapi-generator.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/openapi-generator.md)** - OpenAPI 3.1 spec generator
- **[docs/swagger-ui.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/swagger-ui.md)** - Embedded local Swagger UI web server
- **[docs/browser-launch.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/browser-launch.md)** - Multi-OS browser launcher with health pings
- **[docs/ai-enrichment.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/ai-enrichment.md)** - Optional AI documentation enrichment (`SWAGGER_AI_API_KEY`)
- **[docs/demo.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/demo.md)** - Automatic new API detection verification
- **[docs/project-analysis.md](file:///c:/Users/Soyeb/Desktop/swagger/docs/project-analysis.md)** - Project architecture analysis report

---

## 🧪 Unit Test Suite (17 Tests)
```bash
mvn clean test -f swagger-demo/pom.xml
```
