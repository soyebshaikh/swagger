package com.swagger.demo;

import com.swagger.demo.ai.AiDocumentationService;
import com.swagger.demo.ai.DocumentationMetadata;
import com.swagger.demo.generator.OpenApi3SpecGenerator;
import com.swagger.demo.model.ApiEndpoint;
import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.model.ModelField;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AiDocumentationServiceTest {

    @TempDir
    File tempDir;

    private List<ApiEndpoint> sampleEndpoints;
    private List<ApiModelSchema> sampleSchemas;

    @BeforeEach
    public void setUp() {
        sampleEndpoints = new ArrayList<>();
        sampleSchemas = new ArrayList<>();

        ApiEndpoint ep = new ApiEndpoint("AccountResource", "com.bank.resource", "getBalance", "GET", "/accounts/balance", null, "Account");
        ep.getParameters().add(new ApiEndpoint.ApiParameter("accountNumber", "String", "QueryParam", true));
        sampleEndpoints.add(ep);

        ApiModelSchema schema = new ApiModelSchema("Account", "com.bank.model");
        schema.getFields().add(new ModelField("accountNumber", "String", null, false, false, false, true, true, true));
        schema.getFields().add(new ModelField("balance", "BigDecimal", null, false, false, false, true, true, true));
        sampleSchemas.add(schema);
    }

    @Test
    @DisplayName("Test 1: Graceful Fallback When SWAGGER_AI_API_KEY Is Missing")
    public void testAiServiceDisabledFallback() {
        AiDocumentationService aiService = new AiDocumentationService(null);
        assertFalse(aiService.isAiEnrichmentEnabled());

        Map<String, DocumentationMetadata> epMeta = aiService.generateEndpointDocumentation(sampleEndpoints);
        assertNotNull(epMeta);
        assertTrue(epMeta.containsKey("/accounts/balance:GET"));

        DocumentationMetadata meta = epMeta.get("/accounts/balance:GET");
        assertNotNull(meta);
        assertEquals("Endpoint: getBalance", meta.getSummary());

        Map<String, DocumentationMetadata> modelMeta = aiService.generateModelDocumentation(sampleSchemas);
        assertNotNull(modelMeta);
        assertTrue(modelMeta.containsKey("Account"));
    }

    @Test
    @DisplayName("Test 2: AI Documentation Enrichment When API Key Is Present")
    public void testAiServiceEnabled() {
        AiDocumentationService aiService = new AiDocumentationService("mock-ai-api-key-12345");
        assertTrue(aiService.isAiEnrichmentEnabled());

        Map<String, DocumentationMetadata> epMeta = aiService.generateEndpointDocumentation(sampleEndpoints);
        assertNotNull(epMeta);
        assertTrue(epMeta.containsKey("/accounts/balance:GET"));

        DocumentationMetadata meta = epMeta.get("/accounts/balance:GET");
        assertNotNull(meta);
        assertEquals("Bank Account & Transaction Services", meta.getSummary());
        assertTrue(meta.getTags().contains("Account Management"));
    }

    @Test
    @DisplayName("Test 3: Merge AI Documentation Metadata Into OpenAPI Spec")
    public void testMergeAiMetadataIntoOpenApi() {
        AiDocumentationService aiService = new AiDocumentationService("mock-ai-api-key-12345");
        Map<String, DocumentationMetadata> epMeta = aiService.generateEndpointDocumentation(sampleEndpoints);
        Map<String, DocumentationMetadata> modelMeta = aiService.generateModelDocumentation(sampleSchemas);

        OpenApi3SpecGenerator generator = new OpenApi3SpecGenerator(tempDir);
        OpenAPI openAPI = generator.buildOpenApiDocument(sampleEndpoints, sampleSchemas, epMeta, modelMeta);

        assertNotNull(openAPI.getPaths());
        Operation getOp = openAPI.getPaths().get("/accounts/balance").getGet();
        assertNotNull(getOp);
        assertEquals("Bank Account & Transaction Services", getOp.getSummary());
        assertTrue(getOp.getTags().contains("Account Management"));

        Schema accountSchema = openAPI.getComponents().getSchemas().get("Account");
        assertNotNull(accountSchema);
        assertEquals("Domain data transfer object representing Account entity.", accountSchema.getDescription());
    }
}
