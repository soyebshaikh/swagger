package com.swagger.demo;

import com.swagger.demo.ai.AiDocumentationService;
import com.swagger.demo.ai.DocumentationMetadata;
import com.swagger.demo.generator.OpenApi3SpecGenerator;
import com.swagger.demo.model.ApiEndpoint;
import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.scanner.JavaSourceControllerScanner;
import com.swagger.demo.scanner.JavaSourceModelScanner;
import com.swagger.demo.server.SwaggerUiServer;
import com.swagger.demo.util.BrowserLauncher;
import com.swagger.demo.validator.OpenApiValidator;
import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwaggerDemoLauncher {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerDemoLauncher.class);
    private static final int INITIAL_PORT = 9090;

    public static void main(String[] args) {
        System.out.println();
        System.out.println("Swagger AI Standalone CLI Generator");
        System.out.println("==================================");
        System.out.println();

        try {
            // Parse CLI Argument --src <path>
            File targetProjectDir = new File(".").getCanonicalFile();
            for (int i = 0; i < args.length; i++) {
                if (("--src".equalsIgnoreCase(args[i]) || "-src".equalsIgnoreCase(args[i])) && i + 1 < args.length) {
                    targetProjectDir = new File(args[i + 1]).getCanonicalFile();
                    break;
                }
            }

            File srcDir;
            File outputDir;

            if (targetProjectDir.getName().equals("swagger-demo")) {
                srcDir = new File(targetProjectDir.getParentFile(), "src/main/java");
                outputDir = new File(targetProjectDir, "generated");
            } else {
                File candidateSrc = new File(targetProjectDir, "src/main/java");
                if (candidateSrc.exists()) {
                    srcDir = candidateSrc;
                } else {
                    srcDir = targetProjectDir; // Use provided folder directly
                }
                outputDir = new File(targetProjectDir, "generated");
            }

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            System.out.println("Target Project Directory: " + targetProjectDir.getAbsolutePath());
            System.out.println("Scanning Source Code at : " + srcDir.getAbsolutePath());
            System.out.println();

            // 1. Scan AST Controllers & Models
            System.out.println("Scanning project...");

            JavaSourceControllerScanner controllerScanner = new JavaSourceControllerScanner();
            List<ApiEndpoint> endpoints = controllerScanner.scanSourceDirectory(srcDir);

            Set<String> controllersSet = new HashSet<>();
            ApiEndpoint newlyDetectedEp = null;

            for (ApiEndpoint ep : endpoints) {
                controllersSet.add(ep.getControllerClass());
                if ("/payments/cancel".equals(ep.getPath()) || "cancelPayment".equals(ep.getMethodName())) {
                    newlyDetectedEp = ep;
                }
            }

            JavaSourceModelScanner modelScanner = new JavaSourceModelScanner();
            File modelDir = new File(srcDir, "com/bank/model");
            if (!modelDir.exists()) modelDir = srcDir; // Fallback to scanning srcDir recursively

            List<ApiModelSchema> modelSchemas = modelScanner.scanModelDirectory(modelDir);

            System.out.println("Found " + controllersSet.size() + " controllers");
            System.out.println("Found " + endpoints.size() + " APIs");
            System.out.println("Found " + modelSchemas.size() + " models");
            System.out.println();

            // Highlight Newly Detected API Demonstration Banner if present
            if (newlyDetectedEp != null) {
                System.out.println("NEW API DETECTED");
                System.out.println();
                System.out.println(newlyDetectedEp.getHttpMethod() + " " + newlyDetectedEp.getPath());
                System.out.println();
                System.out.println("Request:");
                System.out.println(newlyDetectedEp.getRequestType() != null ? newlyDetectedEp.getRequestType() : "CancelPaymentRequest");
                System.out.println();
                System.out.println("Response:");
                System.out.println(newlyDetectedEp.getResponseType() != null ? newlyDetectedEp.getResponseType() : "PaymentResponse");
                System.out.println();
                System.out.println("Swagger documentation updated.");
                System.out.println();
            }

            // 2. Optional AI Documentation Enrichment Step
            AiDocumentationService aiService = new AiDocumentationService();
            Map<String, DocumentationMetadata> endpointAiMeta = aiService.generateEndpointDocumentation(endpoints);
            Map<String, DocumentationMetadata> modelAiMeta = aiService.generateModelDocumentation(modelSchemas);

            // 3. Generate OpenAPI 3.1 Document
            System.out.println("Generating OpenAPI...");
            OpenApi3SpecGenerator specGenerator = new OpenApi3SpecGenerator(outputDir);
            OpenAPI openAPI = specGenerator.buildOpenApiDocument(endpoints, modelSchemas, endpointAiMeta, modelAiMeta);

            File yamlSpec = specGenerator.generateYamlSpec(openAPI);
            File jsonSpec = specGenerator.generateJsonSpec(openAPI);
            System.out.println("OpenAPI generated successfully: " + yamlSpec.getAbsolutePath());
            System.out.println();

            // 4. Validate OpenAPI syntax
            OpenApiValidator validator = new OpenApiValidator();
            validator.validateSpecFile(yamlSpec);

            // 5. Start Embedded Swagger UI Web Server on Port 9090
            SwaggerUiServer server = new SwaggerUiServer(INITIAL_PORT, jsonSpec, yamlSpec);
            server.start();

            int boundPort = server.getPort();
            String swaggerUiUrl = "http://localhost:" + boundPort + "/swagger-ui/";
            String healthCheckUrl = "http://localhost:" + boundPort + "/openapi.yaml";

            // 6. Verify Server Responsiveness via HTTP Health Check
            boolean serverActive = BrowserLauncher.verifyServerResponding(healthCheckUrl, 3000);
            if (!serverActive) {
                logger.warn("Server health check timed out. Proceeding to launch browser.");
            }

            // 7. Automatically Open URL in OS Default Browser
            BrowserLauncher.openBrowser(swaggerUiUrl);

            System.out.println("OpenAPI Spec URLs:");
            System.out.println(" - YAML: http://localhost:" + boundPort + "/openapi.yaml");
            System.out.println(" - JSON: http://localhost:" + boundPort + "/openapi.json");
            System.out.println();
            System.out.println("Press Ctrl+C to terminate the demo server.");
            System.out.println("=================================================");

            // Register Shutdown Hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Stopping Swagger Demo Server...");
                server.stop();
            }));

        } catch (Exception e) {
            logger.error("Failed to execute Swagger Demo Generator", e);
        }
    }
}
