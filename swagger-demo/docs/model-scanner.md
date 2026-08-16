# Static Java Model Source AST Scanner

This document describes how Java model and schema detection works inside the `swagger-demo` module.

---

## 1. Schema Detection Architecture

The model scanner uses **JavaParser AST (`javaparser-core`)** to inspect Java request/response models, DTOs, and domain POJOs directly from `.java` source code files without executing or compiling the application.

```text
src/main/java/com/bank/model/*.java
         │
         ▼  (JavaParser AST AST CompilationUnit)
ClassOrInterfaceDeclaration & FieldDeclaration
         │
         ▼  (Field, Type & Generic Inspection)
• Type Resolution (String, BigDecimal, Long, LocalDateTime, etc.)
• Generic Collection Parsing (List<T>, Set<T>, Map<K,V>)
• Getter / Setter Method Verification
• Nested Model Reference Tracking
• Nullability Determination (Primitive vs Object Wrapper)
         │
         ▼  (Internal Schema Construction)
ApiModelSchema & ModelField Data Models
```

---

## 2. Supported Data Types & Generics

| Category | Supported Java Types | Swagger/OpenAPI Type Mapping |
| :--- | :--- | :--- |
| **Primitives & Wrappers** | `int`, `Integer`, `long`, `Long` | `integer` (`int32`, `int64`) |
| **Floating Point** | `float`, `Float`, `double`, `Double` | `number` (`float`, `double`) |
| **High Precision Currency** | `BigDecimal` | `number` (`decimal` / `double`) |
| **Text** | `String`, `char` | `string` |
| **Boolean** | `boolean`, `Boolean` | `boolean` |
| **Date & Time** | `Date`, `LocalDate`, `LocalDateTime` | `string` (`date`, `date-time`) |
| **Generic Collections** | `List<T>`, `Set<T>`, `Collection<T>` | `array` with items type `T` |
| **Maps & Dictionaries** | `Map<K, V>` | `object` with additionalProperties `V` |
| **Nested Models** | `User`, `Account`, `Loan`, `Investment`, `MutualFund` | `$ref: '#/components/schemas/ModelName'` |

---

## 3. Unit Test Verification Coverage

The scanner capabilities are verified using JUnit 5 unit tests in `com.swagger.demo.ModelScannerTest`:

1. **`testSimpleModel()`**: Verifies parsing simple POJOs (`User`) and extracting field getters and setters.
2. **`testNestedModel()`**: Verifies detecting nested model class references (`Investment` referencing `MutualFund`).
3. **`testListModel()`**: Verifies parsing `List<T>` generic collections and extracting inner generic type `T`.
4. **`testBigDecimalField()`**: Verifies `BigDecimal` currency type resolution.
5. **`testNullableFields()`**: Verifies primitive types (`long`, `boolean`) are marked `nullable=false` while Object wrappers (`Integer`, `String`) are marked `nullable=true`.
6. **`testProjectModelDirectoryScan()`**: Scans actual project models in `src/main/java/com/bank/model`.

---

## 4. How to Run Unit Tests

Execute via Maven:
```bash
mvn test -f swagger-demo/pom.xml
```
