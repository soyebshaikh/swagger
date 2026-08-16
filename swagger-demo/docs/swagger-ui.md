# Local Standalone Swagger UI Integration

This document describes how the isolated local Swagger UI web server is started and served in the `swagger-demo` module.

---

## 1. Flow & Isolation Architecture

The Swagger UI integration operates in **100% isolation** from the main Spring banking application.

```text
generated/openapi.yaml
        │
        ▼  (Served by SwaggerUiServer JDK HttpServer)
Local HTTP Web Server (Port 9090)
        │
        ├─► GET /openapi.yaml  (Exposes YAML Specification)
        ├─► GET /openapi.json  (Exposes JSON Specification)
        └─► GET /swagger-ui/   (Interactive Swagger UI Web Interface)
                │
                ▼  (Auto-launched via Desktop.browse)
       System Web Browser
```

### Critical Non-Invasive Rules:
- **No Spring App Modifications**: The main banking backend (Spring XML 5.3.31) has zero dependencies on Swagger UI or web server components.
- **No Extra Ports on Main App**: The main application port is unaffected; Swagger UI runs on dedicated port **9090**.
- **Self-Contained Cleanup**: The entire web server and UI assets reside inside `swagger-demo/` and can be deleted by removing the directory.

---

## 2. Server Configuration Details

- **Target URL**: [`http://localhost:9090/swagger-ui/`](http://localhost:9090/swagger-ui/)
- **Port**: `9090` (with dynamic fallback to 9091, 9092, etc. if port is occupied).
- **OpenAPI Document Source**: Automatically loaded from `generated/openapi.yaml`.
- **UI Engine**: Lightweight Swagger UI v5 standalone web bundle embedded inside `SwaggerUiServer.java`.

---

## 3. How Swagger UI is Started

Run via Maven from the project root:
```bash
mvn compile exec:java -f swagger-demo/pom.xml
```

### Console Startup Log Example:
```text
21:01:30.120 [main] INFO  c.s.d.g.OpenApi3SpecGenerator - Successfully exported OpenAPI YAML spec to: C:\Users\Soyeb\Desktop\swagger\swagger-demo\generated\openapi.yaml
21:01:30.145 [main] INFO  com.swagger.demo.validator.OpenApiValidator - OpenAPI specification in openapi.yaml is syntactically VALID!
21:01:30.160 [main] INFO  com.swagger.demo.server.SwaggerUiServer - Swagger UI HTTP Server started at: http://localhost:9090/swagger-ui/
21:01:30.160 [main] INFO  com.swagger.demo.server.SwaggerUiServer - OpenAPI YAML Specification available at: http://localhost:9090/openapi.yaml
21:01:30.175 [main] INFO  com.swagger.demo.util.BrowserLauncher - Browser launched successfully via Desktop.browse()
=================================================
Swagger Demo Server is Running!
Swagger UI URL: http://localhost:9090/swagger-ui/
OpenAPI YAML : http://localhost:9090/openapi.yaml
OpenAPI JSON : http://localhost:9090/openapi.json
Press Ctrl+C to terminate the demo server.
=================================================
```
