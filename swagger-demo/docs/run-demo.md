# Single-Command Swagger Demo Launcher

This document describes how to execute the complete end-to-end **Swagger AI Demo** using a single command.

---

## 1. Single Command Developer Experience

To run the complete demo pipeline (scanning Java source, detecting endpoints and models, generating OpenAPI 3.1, validating syntax, starting Swagger UI, and opening the browser), run:

```bash
mvn compile exec:java -f swagger-demo/pom.xml
```

---

## 2. Pipeline Execution Steps

When you execute the command, the demo automatically performs:

1. **Java Source Controller Scanning**: Recursively scans `.java` files for Spring MVC / JAX-RS REST annotations.
2. **Java Source Model Scanning**: Recursively scans `.java` POJOs/DTOs for fields, data types, generics, and nested relations.
3. **OpenAPI 3.1 Generation**: Builds `generated/openapi.yaml` and `generated/openapi.json`.
4. **Syntax Validation**: Runs Swagger Parser (`OpenAPIV3Parser`) to confirm syntactical validity.
5. **Web Server & UI Launch**: Starts embedded JDK HTTP server on port `9090` serving `/swagger-ui/` and `/openapi.yaml`.
6. **Browser Auto-Open**: Automatically launches the system browser to the Swagger UI page.

---

## 3. Expected Console Output

```text
Swagger AI Demo
==============

Scanning project...
Found 4 controllers
Found 12 APIs
Found 6 models

Generating OpenAPI...
OpenAPI generated successfully: C:\Users\Soyeb\Desktop\swagger\swagger-demo\generated\openapi.yaml

Validating OpenAPI...
OpenAPI specification in openapi.yaml is syntactically VALID!

Swagger UI:
http://localhost:9090/swagger-ui/

OpenAPI Spec URLs:
 - YAML: http://localhost:9090/openapi.yaml
 - JSON: http://localhost:9090/openapi.json

Press Ctrl+C to terminate the demo server.
=================================================
```

---

## 4. Deleting / Cleaning Up the Demo

Because the demo generator resides 100% inside `swagger-demo/` with zero modifications to the main application code or Spring context, you can remove the entire demo at any time:

```bash
rm -rf swagger-demo
```
