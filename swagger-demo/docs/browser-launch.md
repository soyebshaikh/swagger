# Cross-Platform Browser Launch & Server Verification

This document describes how the `swagger-demo` module verifies HTTP server health and automatically opens the Swagger UI in the operating system default browser.

---

## 1. Execution Flow & Server Verification

```text
SwaggerUiServer.start()
           │
           ▼
BrowserLauncher.verifyServerResponding("http://localhost:9090/openapi.yaml", 3000)
           │ (HTTP GET Health Check Loop via HttpURLConnection)
           ▼ (HTTP 200 OK Confirmed)
Print Required Console Output:
  Swagger UI started.

  Opening:
  http://localhost:9090/swagger-ui/
           │
           ▼
Browser Launch Strategy Execution
```

---

## 2. Multi-OS Browser Launch Strategies

To ensure universal compatibility across developer environments, `BrowserLauncher.java` implements a tiered strategy:

1. **Standard Java Desktop API**:
   - Uses `java.awt.Desktop.getDesktop().browse(new URI("http://localhost:9090/swagger-ui/"))`.
2. **Windows Fallback**:
   - `Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url})`
3. **macOS Fallback**:
   - `Runtime.getRuntime().exec(new String[]{"open", url})`
4. **Linux / Unix Fallback**:
   - `Runtime.getRuntime().exec(new String[]{"xdg-open", url})`

---

## 3. Non-Blocking Failure Safety

If browser auto-opening is unavailable (e.g. running inside headless CI/CD containers, remote SSH servers, or Docker without GUI), the launcher:
- Logs a lightweight warning.
- Prints the Swagger UI URL to stdout for manual copy/paste.
- **Does NOT crash or terminate** the Swagger UI HTTP server.

---

## 4. Console Output Example

```text
Swagger AI Demo
==============

Scanning project...
Found 4 controllers
Found 12 APIs
Found 6 models

Generating OpenAPI...
OpenAPI generated successfully.

Swagger UI started.

Opening:
http://localhost:9090/swagger-ui/

OpenAPI Spec URLs:
 - YAML: http://localhost:9090/openapi.yaml
 - JSON: http://localhost:9090/openapi.json

Press Ctrl+C to terminate the demo server.
=================================================
```
