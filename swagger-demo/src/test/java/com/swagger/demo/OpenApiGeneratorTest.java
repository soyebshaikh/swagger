package com.swagger.demo;

import com.swagger.demo.generator.OpenApi3SpecGenerator;
import com.swagger.demo.model.ApiEndpoint;
import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.model.ModelField;
import com.swagger.demo.scanner.JavaSourceControllerScanner;
import com.swagger.demo.scanner.JavaSourceModelScanner;
import com.swagger.demo.validator.OpenApiValidator;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenApiGeneratorTest {

    private OpenApi3SpecGenerator generator;
    private OpenApiValidator validator;
    private List<ApiEndpoint> sampleEndpoints;
    private List<ApiModelSchema> sampleSchemas;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        generator = new OpenApi3SpecGenerator(tempDir);
        validator = new OpenApiValidator();

        sampleEndpoints = new ArrayList<>();
        sampleSchemas = new ArrayList<>();

        // Setup GET endpoint
        ApiEndpoint getEp = new ApiEndpoint("AccountResource", "com.bank.resource", "getBalance", "GET", "/accounts/balance", null, "Account");
        getEp.getParameters().add(new ApiEndpoint.ApiParameter("accountNumber", "String", "QueryParam", true));
        sampleEndpoints.add(getEp);

        // Setup POST endpoint with path var, request body & response body
        ApiEndpoint postEp = new ApiEndpoint("LoanResource", "com.bank.resource", "applyForLoan", "POST", "/loans/user/{userId}", "Loan", "Loan");
        postEp.getParameters().add(new ApiEndpoint.ApiParameter("userId", "Long", "PathParam", true));
        sampleEndpoints.add(postEp);

        // Setup Model Schemas (Loan and MutualFund nested model)
        ApiModelSchema loanSchema = new ApiModelSchema("Loan", "com.bank.model");
        loanSchema.getFields().add(new ModelField("id", "Long", null, false, false, false, true, true, true));
        loanSchema.getFields().add(new ModelField("amount", "BigDecimal", null, false, false, false, true, true, true));
        loanSchema.getFields().add(new ModelField("fundDetails", "MutualFund", null, false, false, true, true, true, true));
        sampleSchemas.add(loanSchema);

        ApiModelSchema fundSchema = new ApiModelSchema("MutualFund", "com.bank.model");
        fundSchema.getFields().add(new ModelField("fundId", "Long", null, false, false, false, true, true, true));
        fundSchema.getFields().add(new ModelField("fundName", "String", null, false, false, false, true, true, true));
        sampleSchemas.add(fundSchema);
    }

    @Test
    @DisplayName("Test 1: GET Endpoint Generation")
    public void testGetEndpoint() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        assertNotNull(openAPI.getPaths());
        assertTrue(openAPI.getPaths().containsKey("/accounts/balance"));

        PathItem pathItem = openAPI.getPaths().get("/accounts/balance");
        assertNotNull(pathItem.getGet());
        assertEquals("Endpoint: getBalance", pathItem.getGet().getSummary());
    }

    @Test
    @DisplayName("Test 2: POST Endpoint Generation")
    public void testPostEndpoint() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        assertTrue(openAPI.getPaths().containsKey("/loans/user/{userId}"));

        PathItem pathItem = openAPI.getPaths().get("/loans/user/{userId}");
        assertNotNull(pathItem.getPost());
        assertEquals("Endpoint: applyForLoan", pathItem.getPost().getSummary());
    }

    @Test
    @DisplayName("Test 3: Path Variable Extraction & Schema Mapping")
    public void testPathVariable() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        Operation postOp = openAPI.getPaths().get("/loans/user/{userId}").getPost();

        assertNotNull(postOp.getParameters());
        Parameter pathParam = postOp.getParameters().stream().filter(p -> "userId".equals(p.getName())).findFirst().orElse(null);
        assertNotNull(pathParam);
        assertEquals("path", pathParam.getIn());
        assertTrue(pathParam.getRequired());
        assertEquals("integer", pathParam.getSchema().getType());
        assertEquals("int64", pathParam.getSchema().getFormat());
    }

    @Test
    @DisplayName("Test 4: Query Parameter Extraction & Schema Mapping")
    public void testQueryParameter() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        Operation getOp = openAPI.getPaths().get("/accounts/balance").getGet();

        assertNotNull(getOp.getParameters());
        Parameter queryParam = getOp.getParameters().stream().filter(p -> "accountNumber".equals(p.getName())).findFirst().orElse(null);
        assertNotNull(queryParam);
        assertEquals("query", queryParam.getIn());
        assertEquals("string", queryParam.getSchema().getType());
    }

    @Test
    @DisplayName("Test 5: Request Body Generation with $ref Schema")
    public void testRequestBody() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        Operation postOp = openAPI.getPaths().get("/loans/user/{userId}").getPost();

        assertNotNull(postOp.getRequestBody());
        assertTrue(postOp.getRequestBody().getContent().containsKey("application/json"));
        Schema schema = postOp.getRequestBody().getContent().get("application/json").getSchema();
        assertEquals("#/components/schemas/Loan", schema.get$ref());
    }

    @Test
    @DisplayName("Test 6: Response Body Generation with 200 OK Content")
    public void testResponseBody() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        Operation postOp = openAPI.getPaths().get("/loans/user/{userId}").getPost();

        assertNotNull(postOp.getResponses());
        assertTrue(postOp.getResponses().containsKey("200"));
        assertTrue(postOp.getResponses().get("200").getContent().containsKey("application/json"));
    }

    @Test
    @DisplayName("Test 7: Nested Model $ref Schema Mapping")
    public void testNestedModel() {
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas);
        assertNotNull(openAPI.getComponents().getSchemas().get("Loan"));

        Schema loanSchema = openAPI.getComponents().getSchemas().get("Loan");
        assertNotNull(loanSchema.getProperties());
        assertTrue(loanSchema.getProperties().containsKey("fundDetails"));

        Schema fundDetailsProp = (Schema) loanSchema.getProperties().get("fundDetails");
        assertEquals("#/components/schemas/MutualFund", fundDetailsProp.get$ref());
    }

    @Test
    @DisplayName("Test 8: Syntactical Validation of Generated openapi.yaml Spec")
    public void testOpenApiSyntaxValidation() throws Exception {
        // Run full real AST scan on banking project source
        File srcDir = new File("../src/main/java");
        if (!srcDir.exists()) srcDir = new File("src/main/java");

        JavaSourceControllerScanner controllerScanner = new JavaSourceControllerScanner();
        JavaSourceModelScanner modelScanner = new JavaSourceModelScanner();

        List<ApiEndpoint> endpoints = controllerScanner.scanSourceDirectory(srcDir);
        List<ApiModelSchema> schemas = modelScanner.scanModelDirectory(new File(srcDir, "com/bank/model"));

        OpenAPI openAPI = generator.buildOpenApiDocument(endpoints, schemas);

        File yamlSpec = generator.generateYamlSpec(openAPI);
        File jsonSpec = generator.generateJsonSpec(openAPI);

        assertTrue(yamlSpec.exists());
        assertTrue(jsonSpec.exists());

        boolean isYamlValid = validator.validateSpecFile(yamlSpec);
        assertTrue(isYamlValid, "Generated openapi.yaml must pass OpenAPI v3 syntax validation");
    }
}
