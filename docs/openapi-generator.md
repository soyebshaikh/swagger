# OpenAPI 3.1 Spec Generator & Validator

This document describes the design and implementation of the **OpenAPI 3.1 Specification Generator** in the `swagger-demo` module.

---

## 1. Spec Generation Architecture

The spec generator integrates output from the static Java AST scanners:
- **`JavaSourceControllerScanner`**: Discovers endpoints, HTTP verbs, paths, path variables, query params, and body parameters.
- **`JavaSourceModelScanner`**: Discovers models, fields, generic types (`List<T>`, `Set<T>`, `Map<K,V>`), and nested model relations.

```text
Java AST Scanners (Controllers & Models)
                   │
                   ▼
         OpenApi3SpecGenerator
                   │
       ┌───────────┴───────────┐
       ▼                       ▼
Build Component Schemas  Build Path Items & Operations
($ref Model References)   (Parameters, RequestBody, Responses)
       │                       │
       └───────────┬───────────┘
                   ▼
     Export generated/openapi.yaml
                   │
                   ▼
 OpenApiValidator (Swagger Parser Validation)
```

---

## 2. Java Type to OpenAPI Data Type Mapping Table

The generator converts Java data types to exact OpenAPI specification data types and formats:

| Java Type | OpenAPI Type | OpenAPI Format | Example Schema Output |
| :--- | :--- | :--- | :--- |
| `String`, `char` | `string` | — | `{"type": "string"}` |
| `Integer`, `int` | `integer` | `int32` | `{"type": "integer", "format": "int32"}` |
| `Long`, `long` | `integer` | `int64` | `{"type": "integer", "format": "int64"}` |
| `Boolean`, `boolean` | `boolean` | — | `{"type": "boolean"}` |
| `Double`, `double` | `number` | `double` | `{"type": "number", "format": "double"}` |
| `Float`, `float` | `number` | `float` | `{"type": "number", "format": "float"}` |
| `BigDecimal` | `number` | — | `{"type": "number"}` |
| `Date`, `LocalDate` | `string` | `date` | `{"type": "string", "format": "date"}` |
| `LocalDateTime` | `string` | `date-time` | `{"type": "string", "format": "date-time"}` |
| `List<T>`, `Set<T>` | `array` | — | `{"type": "array", "items": {"$ref": "..."}}` |
| `Map<K, V>` | `object` | — | `{"type": "object"}` |
| `User`, `Account`, etc. | `$ref` | — | `{"$ref": "#/components/schemas/User"}` |

---

## 3. OpenAPI 3.1 YAML Document Structure

The generated `swagger-demo/generated/openapi.yaml` follows the structure:

```yaml
openapi: 3.1.0
info:
  title: Apex Banking Application REST API
  description: Auto-generated OpenAPI 3.1 specification constructed dynamically from AST Java source scanners.
  version: 1.0.0
servers:
  - url: http://localhost:8088
    description: Banking Application Backend Server
paths:
  /auth/login:
    post:
      summary: Endpoint: login
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: Successful Operation
  /accounts/balance:
    get:
      summary: Endpoint: getBalance
      parameters:
        - name: accountNumber
          in: query
          required: true
          schema:
            type: string
  /loans/user/{userId}:
    get:
      summary: Endpoint: getLoansByUser
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
            format: int64
components:
  schemas:
    Loan:
      type: object
      properties:
        id:
          type: integer
          format: int64
        amount:
          type: number
        fundDetails:
          $ref: '#/components/schemas/MutualFund'
    MutualFund:
      type: object
      properties:
        fundId:
          type: integer
          format: int64
        fundName:
          type: string
```

---

## 4. Test Verification Suite (`OpenApiGeneratorTest`)

JUnit 5 test suite verifies:
1. `testGetEndpoint`: GET method and path mapping.
2. `testPostEndpoint`: POST method and path mapping.
3. `testPathVariable`: Path variable (`{userId}`) integer/int64 format mapping.
4. `testQueryParameter`: Query parameter (`accountNumber`) string format mapping.
5. `testRequestBody`: Request body payload with `$ref: '#/components/schemas/Loan'`.
6. `testResponseBody`: 200 OK JSON response payload schema.
7. `testNestedModel`: `$ref: '#/components/schemas/MutualFund'` nested schema references.
8. `testOpenApiSyntaxValidation`: Validates generated `openapi.yaml` with `OpenAPIV3Parser`.

---

## 5. Running the Spec Generator & Tests

Run unit tests via Maven:
```bash
mvn clean test -f swagger-demo/pom.xml
```
