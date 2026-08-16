# Standalone Swagger Generation Demo Architecture

This document describes the design and complete isolation of the proof-of-concept **Swagger Generation Demo** built for the Banking Application.

---

## 1. Zero-Impact Isolation Architecture

The Swagger generator is built inside a completely separate, self-contained directory `swagger-demo/` with its own `pom.xml`.

```text
swagger/                      # Main Project Root
├── src/                      # Original Application Source Code (Unmodified)
├── pom.xml                   # Original Project Build Configuration (Unmodified)
└── swagger-demo/             # Standalone Swagger Generator Demo (Isolated)
    ├── pom.xml               # Independent Maven Build File
    ├── generated/            # Auto-generated openapi.json and openapi.yaml
    └── src/main/java/com/swagger/demo/
        ├── SwaggerDemoLauncher.java   # Executable Main Entry Point
        ├── scanner/
        │   └── JaxRsApiScanner.java    # Reflection scanner for REST endpoints
        ├── generator/
        │   └── OpenApiSpecGenerator.java # OpenAPI 3.0 Document Builder & Exporter
        ├── server/
        │   └── SwaggerUiServer.java    # Embedded JDK HTTP Web Server for Swagger UI
        └── util/
            └── BrowserLauncher.java    # Automated Web Browser Opener
```

### Critical Non-Invasive Rules Followed:
- **No Source Code Annotations**: Source code files in `src/main/java/com/bank/` were **NOT** touched or annotated with `@Api`, `@ApiOperation`, or `@ApiResponse`.
- **No Spring Boot Migration**: The main application remains on pure Spring XML 5.3.31.
- **No Config Alterations**: `applicationContext.xml`, `spring-mybatis.xml`, `camel-context.xml`, and `schema.sql` remain completely unmodified.

---

## 2. Dynamic Discovery & Execution Pipeline

```text
Java REST Source Code (com.bank.resource)
          │
          ▼  (Reflection Scan via JaxRsApiScanner)
   Discovers Endpoints: @Path, @GET, @POST, @Produces, @Consumes
          │
          ▼  (OpenApiSpecGenerator)
  Constructs OpenAPI 3.0 Document Object
          │
          ▼  (Outputs Spec Files)
  swagger-demo/generated/openapi.json & openapi.yaml
          │
          ▼  (SwaggerUiServer)
  Launches Embedded JDK HttpServer on http://localhost:8088/swagger-ui
          │
          ▼  (BrowserLauncher)
  Automatically Launches System Browser to Display Interactive Swagger UI
```

---

## 3. Discovered REST Endpoints in Demo

The scanner dynamically detects and documents the following JAX-RS endpoints:

1. **Authentication (`/auth`)**:
   - `POST /auth/login` - User Login
   - `POST /auth/register` - Customer Registration
2. **Accounts & Balance (`/accounts`)**:
   - `GET /accounts/balance` - Balance Check
   - `GET /accounts/details` - Account Details
   - `POST /accounts/withdraw` - Withdrawal Transaction
   - `POST /accounts/deposit` - Deposit Transaction
   - `GET /accounts/transactions` - Transaction History
3. **Loans (`/loans`)**:
   - `POST /loans/apply` - Loan Application
   - `GET /loans/user/{userId}` - Customer Loan Records
4. **Investments & Mutual Funds (`/investments`)**:
   - `GET /investments/funds` - Mutual Fund Catalog
   - `POST /investments/buy` - Mutual Fund Purchase
   - `GET /investments/user/{userId}` - Portfolio Summary

---

## 4. How to Run the Demo

### Run via Maven:
From the project root directory, run:
```bash
mvn compile exec:java -f swagger-demo/pom.xml
```

### What Happens When Run:
1. The reflection scanner scans `com.bank.resource.*`.
2. Formatted OpenAPI specifications are saved to `swagger-demo/generated/openapi.json` and `openapi.yaml`.
3. The embedded JDK web server starts listening on `http://localhost:8088/swagger-ui`.
4. Your default web browser automatically opens showing the interactive Swagger UI.

---

## 5. Deletion & Cleanup Instructions

Because this demo is 100% isolated inside the `swagger-demo/` directory, it can be deleted at any time without affecting the main banking backend application.

To delete the demo completely:
```bash
rm -rf swagger-demo
rm -f docs/swagger-demo-architecture.md
```
