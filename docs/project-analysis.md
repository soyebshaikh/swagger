# Project Architecture

## Overview
The project is a standard single-module Maven backend application built with **Spring Framework 5.3.31** (non-Spring Boot), using **XML-based Spring Bean Configuration**, **MyBatis 3.5.15** for ORM persistence with H2 embedded database, **Apache Camel 3.22.2** for integration workflows, and **JAX-RS (Jersey 2.39.1)** for HTTP REST endpoints.

---

## Spring Configuration
- **Configuration Style**: Pure XML-based bean configuration without Spring Boot auto-configuration or `<context:component-scan>`.
- **Primary Files**:
  - `src/main/resources/applicationContext.xml`: Master Spring context importing `spring-mybatis.xml` and `camel-context.xml`. Declares explicit `<bean>` tags for services (`authService`, `accountService`, `loanService`, `investmentService`), Camel routes (`bankingCamelRoutes`), and JAX-RS resources (`authResource`, `accountResource`, `loanResource`, `investmentResource`).
  - `src/main/resources/spring-mybatis.xml`: Configures embedded H2 database (`<jdbc:embedded-database>`), `SqlSessionFactoryBean`, `MapperScannerConfigurer`, and `DataSourceTransactionManager`.
  - `src/main/resources/camel-context.xml`: Configures Apache Camel context (`bankingCamelContext`) and XML integration routes.
- **Application Initialization**: Loaded via `ClassPathXmlApplicationContext("applicationContext.xml")` in [MainApplication.java](file:///c:/Users/Soyeb/Desktop/swagger/src/main/java/com/bank/MainApplication.java) and `@ContextConfiguration(locations = {"classpath:applicationContext.xml"})` in Spring JUnit 5 integration tests.

---

## Controller Architecture
- **Framework**: JAX-RS (`javax.ws.rs-api` 2.1.1 + `jersey-server` 2.39.1).
- **Location**: `com.bank.resource`
- **Classes**:
  - `AuthResource`: Annotated with `@Path("/auth")`, `@Produces(MediaType.APPLICATION_JSON)`, `@Consumes(MediaType.APPLICATION_JSON)`. Defines `@POST @Path("/login")` and `@POST @Path("/register")`.
  - `AccountResource`: Annotated with `@Path("/accounts")`, `@Produces(MediaType.APPLICATION_JSON)`, `@Consumes(MediaType.APPLICATION_JSON)`. Defines `@GET @Path("/balance")`, `@GET @Path("/details")`, `@POST @Path("/withdraw")`, `@POST @Path("/deposit")`, `@GET @Path("/transactions")`.
  - `LoanResource`: Annotated with `@Path("/loans")`, `@Produces(MediaType.APPLICATION_JSON)`, `@Consumes(MediaType.APPLICATION_JSON)`. Defines `@POST @Path("/apply")` and `@GET @Path("/user/{userId}")`.
  - `InvestmentResource`: Annotated with `@Path("/investments")`, `@Produces(MediaType.APPLICATION_JSON)`, `@Consumes(MediaType.APPLICATION_JSON)`. Defines `@GET @Path("/funds")`, `@POST @Path("/buy")`, `@GET @Path("/user/{userId}")`.
- **Definition Style**: Standard POJOs annotated with JAX-RS routing annotations without Spring `@Controller` or `@RestController`. Declared as Spring beans via XML constructor injection of respective Service interfaces.

---

## Service Architecture
- **Location**: `com.bank.service`
- **Pattern**: Interface + Implementation design (`AuthService`/`AuthServiceImpl`, `AccountService`/`AccountServiceImpl`, `LoanService`/`LoanServiceImpl`, `InvestmentService`/`InvestmentServiceImpl`).
- **Annotation Style**: Plain Java classes without `@Service`, `@Component`, or `@Autowired`.
- **Dependency Injection**: Dependencies (MyBatis Mapper interfaces and Camel `ProducerTemplate`) are injected strictly via XML constructor and setter declarations in `applicationContext.xml`.

---

## DAO Architecture
- **Location**: `com.bank.mapper`
- **Pattern**: Data Access Object (DAO) pattern implemented via MyBatis Java Mapper Interfaces:
  - `UserMapper`: User queries and registration DML.
  - `AccountMapper`: Account balance updates and user lookup.
  - `TransactionMapper`: Audit transaction persistence.
  - `LoanMapper`: Loan application insert and status update.
  - `InvestmentMapper`: Mutual fund catalog queries and user portfolio updates.
- **Scanner**: Discovered by MyBatis-Spring `MapperScannerConfigurer` configured in `spring-mybatis.xml`.

---

## Model Architecture
- **Location**: `com.bank.model`
- **Classes**: Plain Old Java Objects (POJOs) implementing `Serializable`:
  - `User`: User credentials and user roles.
  - `Account`: Bank account number, type (Checking/Savings), and balance.
  - `Transaction`: Financial transaction reference, transaction type (Deposit/Withdrawal), amount, balance after.
  - `Loan`: Loan application details, principal amount, interest rate, duration, monthly EMI, and status.
  - `MutualFund`: Mutual fund code, category, NAV, and risk level.
  - `Investment`: User mutual fund holding units, invested amount, and current valuation.

---

## MyBatis Architecture
- **Config & Mappers**: `src/main/resources/mappers/*.xml`
- **XML Mapping Files**:
  - `UserMapper.xml`: Maps SQL results to `com.bank.model.User`.
  - `AccountMapper.xml`: Maps SQL results to `com.bank.model.Account`.
  - `TransactionMapper.xml`: Maps SQL results to `com.bank.model.Transaction`.
  - `LoanMapper.xml`: Maps SQL results to `com.bank.model.Loan`.
  - `InvestmentMapper.xml`: Maps joined SQL results to `com.bank.model.Investment` and `MutualFund`.
- **Database Schema & Data**:
  - `src/main/resources/schema.sql`: DDL for tables `users`, `accounts`, `transactions`, `loans`, `mutual_funds`, `investments`.
  - `src/main/resources/data.sql`: Seed data for users, account balances, loans, and mutual funds.

---

## Existing REST APIs

| Resource Endpoint | HTTP Method | Request Body / Query Params | Response Type | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/auth/login` | `POST` | `Map<String, String>` (username, password) | `User` (JSON) | Authenticates user credentials |
| `/auth/register` | `POST` | `User` (JSON) | `User` (JSON) | Registers new customer |
| `/accounts/balance` | `GET` | `accountNumber` (Query Param) | `Map<String, Object>` | Retrieves account balance |
| `/accounts/details` | `GET` | `accountNumber` (Query Param) | `Account` (JSON) | Retrieves account details |
| `/accounts/withdraw` | `POST` | `Map<String, Object>` (accountNumber, amount, description) | `Transaction` (JSON) | Processes cash/online withdrawal |
| `/accounts/deposit` | `POST` | `Map<String, Object>` (accountNumber, amount, description) | `Transaction` (JSON) | Processes deposit |
| `/accounts/transactions` | `GET` | `accountNumber` (Query Param) | `List<Transaction>` | Fetches transaction history |
| `/loans/apply` | `POST` | `Map<String, Object>` (userId, loanType, principalAmount, interestRate, durationMonths) | `Loan` (JSON) | Submits loan application & triggers Camel auto-approval |
| `/loans/user/{userId}` | `GET` | `userId` (Path Variable) | `List<Loan>` | Fetches user loan applications |
| `/investments/funds` | `GET` | None | `List<MutualFund>` | Catalog of available mutual funds |
| `/investments/buy` | `POST` | `Map<String, Object>` (userId, fundCode, amount, investmentType) | `Investment` (JSON) | Purchases mutual fund units |
| `/investments/user/{userId}`| `GET` | `userId` (Path Variable) | `List<Investment>` | Retrieves user investment portfolio |

---

## Existing Swagger/OpenAPI Support
- **Current State**: None. `pom.xml` currently has no dependencies for `swagger-core`, `swagger-jaxrs`, `swagger-ui`, or `smallrye-open-api` / `openapi-maven-plugin`.
- **Source Code Annotations**: Source code is strictly unannotated with `@Api`, `@ApiOperation`, or `@ApiResponse`.

---

## Recommended Demo Integration Point
- **Target Integration**: Attach an automated OpenAPI 3.0 specification generator / Swagger UI provider using Maven plugin generation or runtime Swagger reader bean registered in `applicationContext.xml`.
- **Zero-Code-Modification Approach**:
  1. **Option A (Maven Plugin Specification Generation)**: Use `swagger-maven-plugin` (or `openapi-maven-plugin`) in `pom.xml` configured to scan JAX-RS `@Path` resource classes in `com.bank.resource` during build phase (`mvn compile`) to output `openapi.json` / `openapi.yaml` in `target/classes/META-INF/resources/swagger/`.
  2. **Option B (Camel / JAX-RS Swagger Reader Bean)**: Register a standalone Swagger/OpenAPI Spec Reader bean in `applicationContext.xml` or Camel REST DSL reader component (`camel-openapi-java` / `swagger-java`) configured via XML properties without modifying any Java resource files or business code.
