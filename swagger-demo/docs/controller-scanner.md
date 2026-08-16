# Controller & Endpoint Static AST Scanner

This document explains how static controller and API endpoint detection works inside the `swagger-demo` module.

---

## 1. Detection Architecture

The scanner uses **JavaParser AST (`javaparser-core`)** to parse Java source files (`.java`) directly from disk without compiling bytecode, running Maven builds, or executing the Spring application context.

```text
src/main/java/**/*.java
       │
       ▼  (JavaParser Static Parsing)
CompilationUnit (AST Syntax Tree)
       │
       ▼  (Class & Method Inspection)
ClassOrInterfaceDeclaration & MethodDeclaration
       │
       ▼  (Annotation Detection)
Spring MVC (@Controller, @RestController, @RequestMapping, @GetMapping, @PostMapping...)
JAX-RS (@Path, @GET, @POST, @PUT, @DELETE...)
       │
       ▼  (Metadata Model Construction)
ApiEndpoint List (HTTP Method, URL Path, Controller Method, Parameters)
```

---

## 2. Supported Annotations

### Class Level (Controller Discovery):
- Spring MVC: `@Controller`, `@RestController`, `@RequestMapping("/path")`
- JAX-RS: `@Path("/path")`

### Method Level (HTTP Methods & Mappings):
- Spring MVC: `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
- JAX-RS: `@Path`, `@GET`, `@POST`, `@PUT`, `@DELETE`

### Parameter Level (Query, Path & Body):
- Spring MVC: `@PathVariable`, `@RequestParam`, `@RequestBody`
- JAX-RS: `@PathParam`, `@QueryParam`

---

## 3. Discovered Endpoints Output Example

Running `ControllerScannerCommand` outputs:

```text
====================================================================================================
                         SPRING MVC & JAX-RS CONTROLLER SOURCE SCANNER                              
====================================================================================================

Detected APIs (12 endpoints discovered from AST source scan):
----------------------------------------------------------------------------------------------------
METHOD  PATH                                CONTROLLER METHOD         PARAMETERS                    
----------------------------------------------------------------------------------------------------
POST    /auth/login                         AuthResource.login()      [credentials (Map<String, String>, null)]
POST    /auth/register                      AuthResource.register()   [user (User, null)]           
POST    /accounts/withdraw                  AccountResource.withdraw() [request (Map<String, Object>, null)]
POST    /accounts/deposit                   AccountResource.deposit() [request (Map<String, Object>, null)]
GET     /accounts/balance                   AccountResource.getBalance() [accountNumber (String, @QueryParam)]
GET     /accounts/transactions              AccountResource.getTransactions() [accountNumber (String, @QueryParam)]
GET     /accounts/details                   AccountResource.getAccountDetails() [accountNumber (String, @QueryParam)]
POST    /loans/apply                        LoanResource.applyForLoan() [request (Map<String, Object>, null)]
GET     /loans/user/{userId}                LoanResource.getLoansByUser() [userId (Long, @PathParam)]   
GET     /investments/user/{userId}          InvestmentResource.getUserInvestments() [userId (Long, @PathParam)]
GET     /investments/funds                  InvestmentResource.getAllMutualFunds() [None]                        
POST    /investments/buy                    InvestmentResource.buyMutualFund() [request (Map<String, Object>, null)]
----------------------------------------------------------------------------------------------------
Scan completed successfully! Discovered 12 total API endpoints.
====================================================================================================
```

---

## 4. How to Run the Scanner Command

Execute via Maven:
```bash
mvn compile exec:java -Dexec.mainClass="com.swagger.demo.ControllerScannerCommand" -f swagger-demo/pom.xml
```
