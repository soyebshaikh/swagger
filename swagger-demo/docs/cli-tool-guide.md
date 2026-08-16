# Standalone Executable CLI JAR (`swagger-generator-cli.jar`)

This document describes how to use the standalone executable JAR `swagger-generator-cli.jar` to generate Swagger/OpenAPI 3.1 documentation and launch the Swagger UI for **ANY existing Java project** without copying code, adding annotations, or modifying project files.

---

## 1. Zero-Impact Standalone Advantage

With `swagger-generator-cli.jar`:
- **No Code Copying**: You do NOT need to copy any Java files or sub-folders into your target application.
- **No Code Annotations**: Source files in your application require zero `@Api` or `@ApiOperation` annotations.
- **No Build File Changes**: Your application's `pom.xml` or build file remains 100% clean and untouched.
- **Portable Single File**: The entire scanner, spec generator, syntax validator, and HTTP web server are bundled inside a single `.jar` file.

---

## 2. Where the JAR is Located

After building the demo package, the self-contained executable JAR is created at:

```text
swagger-demo/target/swagger-generator-cli.jar
```

You can copy `swagger-generator-cli.jar` to any convenient location on your system (e.g. `C:\tools\swagger-generator-cli.jar` or `/usr/local/bin/swagger-generator-cli.jar`).

---

## 3. How to Run Against Any Java Project

### Option A: Open Terminal in Target Project Directory
Navigate to the root directory of your target Java project and run:

```bash
java -jar C:\tools\swagger-generator-cli.jar
```

### Option B: Pass Target Project Path via `--src` Argument
Run from anywhere by specifying the target project path:

```bash
java -jar C:\tools\swagger-generator-cli.jar --src C:\projects\my-legacy-app
```

---

## 4. Execution Output Log Example

```text
Swagger AI Standalone CLI Generator
==================================

Target Project Directory: C:\projects\my-legacy-app
Scanning Source Code at : C:\projects\my-legacy-app\src\main\java

Scanning project...
Found 5 controllers
Found 13 APIs
Found 8 models

Generating OpenAPI...
OpenAPI generated successfully: C:\projects\my-legacy-app\generated\openapi.yaml

Validating OpenAPI...
OpenAPI specification in openapi.yaml is syntactically VALID!

Swagger UI started.

Opening:
http://localhost:9090/swagger-ui/

OpenAPI Spec URLs:
 - YAML: http://localhost:9090/openapi.yaml
 - JSON: http://localhost:9090/openapi.json

Press Ctrl+C to terminate the demo server.
=================================================
```
