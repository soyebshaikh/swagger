package com.swagger.demo.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swagger.demo.ai.DocumentationMetadata;
import com.swagger.demo.model.ApiEndpoint;
import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.model.ModelField;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenApi3SpecGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApi3SpecGenerator.class);

    private final File outputDirectory;

    public OpenApi3SpecGenerator(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }

    public OpenAPI buildOpenApiDocument(List<ApiEndpoint> endpoints, List<ApiModelSchema> modelSchemas) {
        return buildOpenApiDocument(endpoints, modelSchemas, new HashMap<>(), new HashMap<>());
    }

    public OpenAPI buildOpenApiDocument(List<ApiEndpoint> endpoints,
                                        List<ApiModelSchema> modelSchemas,
                                        Map<String, DocumentationMetadata> endpointAiMeta,
                                        Map<String, DocumentationMetadata> modelAiMeta) {
        OpenAPI openAPI = new OpenAPI();
        openAPI.setOpenapi("3.1.0");

        // Info Metadata
        Info info = new Info()
                .title("Apex Banking Application REST API")
                .version("1.0.0")
                .description("Auto-generated OpenAPI 3.1 specification constructed dynamically from AST Java source scanners.");
        openAPI.setInfo(info);

        // Servers
        Server server = new Server()
                .url("http://localhost:9090")
                .description("Banking Application Backend Server");
        openAPI.setServers(List.of(server));

        // Components & Schemas
        Components components = new Components();
        Map<String, Schema> schemasMap = new HashMap<>();

        for (ApiModelSchema modelSchema : modelSchemas) {
            DocumentationMetadata aiMeta = modelAiMeta.get(modelSchema.getClassName());
            Schema<?> objectSchema = buildObjectSchema(modelSchema, aiMeta);
            schemasMap.put(modelSchema.getClassName(), objectSchema);
        }

        components.setSchemas(schemasMap);
        openAPI.setComponents(components);

        // Paths & Operations
        Paths paths = new Paths();

        for (ApiEndpoint ep : endpoints) {
            String fullPath = ep.getPath();
            PathItem pathItem = paths.get(fullPath);
            if (pathItem == null) {
                pathItem = new PathItem();
                paths.addPathItem(fullPath, pathItem);
            }

            DocumentationMetadata aiMeta = endpointAiMeta.get(ep.getPath() + ":" + ep.getHttpMethod());
            Operation operation = buildOperation(ep, aiMeta);
            String httpMethod = ep.getHttpMethod().toUpperCase();

            switch (httpMethod) {
                case "GET": pathItem.setGet(operation); break;
                case "POST": pathItem.setPost(operation); break;
                case "PUT": pathItem.setPut(operation); break;
                case "DELETE": pathItem.setDelete(operation); break;
                case "PATCH": pathItem.setPatch(operation); break;
                default: pathItem.setGet(operation); break;
            }
        }

        openAPI.setPaths(paths);
        return openAPI;
    }

    public File generateYamlSpec(OpenAPI openAPI) throws IOException {
        File yamlFile = new File(outputDirectory, "openapi.yaml");
        ObjectMapper mapper = Yaml.mapper();
        String yamlContent = mapper.writeValueAsString(openAPI);

        try (FileWriter writer = new FileWriter(yamlFile)) {
            writer.write(yamlContent);
        }

        logger.info("Successfully exported OpenAPI YAML spec to: {}", yamlFile.getAbsolutePath());
        return yamlFile;
    }

    public File generateJsonSpec(OpenAPI openAPI) throws IOException {
        File jsonFile = new File(outputDirectory, "openapi.json");
        ObjectMapper mapper = Json.mapper();
        String jsonContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonContent);
        }

        logger.info("Successfully exported OpenAPI JSON spec to: {}", jsonFile.getAbsolutePath());
        return jsonFile;
    }

    private Schema<?> buildObjectSchema(ApiModelSchema modelSchema, DocumentationMetadata aiMeta) {
        ObjectSchema schema = new ObjectSchema();
        String desc = (aiMeta != null && aiMeta.getDescription() != null) ? aiMeta.getDescription() : "Domain Model Schema for " + modelSchema.getClassName();
        schema.setDescription(desc);

        Map<String, Schema> properties = new HashMap<>();

        for (ModelField field : modelSchema.getFields()) {
            Schema<?> fieldSchema = mapJavaTypeToOpenApiSchema(
                    field.getType(), field.getGenericType(), field.isCollection(), field.isMap(), field.isNestedModel()
            );

            if (aiMeta != null && aiMeta.getFieldDescriptions().containsKey(field.getName())) {
                fieldSchema.setDescription(aiMeta.getFieldDescriptions().get(field.getName()));
            }

            properties.put(field.getName(), fieldSchema);
        }

        schema.setProperties(properties);
        return schema;
    }

    private Operation buildOperation(ApiEndpoint ep, DocumentationMetadata aiMeta) {
        Operation operation = new Operation();

        String summary = (aiMeta != null && aiMeta.getSummary() != null) ? aiMeta.getSummary() : "Endpoint: " + ep.getMethodName();
        String description = (aiMeta != null && aiMeta.getDescription() != null) ? aiMeta.getDescription() : "Discovered automatically from method " + ep.getControllerClass() + "." + ep.getMethodName() + "()";

        operation.setSummary(summary);
        operation.setDescription(description);

        if (aiMeta != null && aiMeta.getTags() != null && !aiMeta.getTags().isEmpty()) {
            operation.setTags(aiMeta.getTags());
        }

        List<Parameter> parameters = new ArrayList<>();

        for (ApiEndpoint.ApiParameter p : ep.getParameters()) {
            Parameter param = null;
            if ("PathParam".equals(p.getAnnotationType()) || "PathVariable".equals(p.getAnnotationType())) {
                param = new PathParameter();
                param.setName(p.getName());
                param.setRequired(true);
                param.setSchema(mapJavaTypeToOpenApiSchema(p.getType(), null, false, false, false));
            } else if ("QueryParam".equals(p.getAnnotationType()) || "RequestParam".equals(p.getAnnotationType())) {
                param = new QueryParameter();
                param.setName(p.getName());
                param.setRequired(p.isRequired());
                param.setSchema(mapJavaTypeToOpenApiSchema(p.getType(), null, false, false, false));
            }

            if (param != null) {
                if (aiMeta != null && aiMeta.getParameterDescriptions().containsKey(p.getName())) {
                    param.setDescription(aiMeta.getParameterDescriptions().get(p.getName()));
                }
                parameters.add(param);
            }
        }

        if (!parameters.isEmpty()) {
            operation.setParameters(parameters);
        }

        // Request Body
        if (ep.getRequestType() != null && ("POST".equalsIgnoreCase(ep.getHttpMethod()) || "PUT".equalsIgnoreCase(ep.getHttpMethod()) || "PATCH".equalsIgnoreCase(ep.getHttpMethod()))) {
            RequestBody requestBody = new RequestBody();
            requestBody.setRequired(true);
            requestBody.setDescription("Request payload of type " + ep.getRequestType());

            Content content = new Content();
            MediaType mediaType = new MediaType();
            mediaType.setSchema(mapJavaTypeToOpenApiSchema(ep.getRequestType(), null, false, false, isCustomModel(ep.getRequestType())));
            content.addMediaType("application/json", mediaType);
            requestBody.setContent(content);

            operation.setRequestBody(requestBody);
        }

        // Api Responses
        ApiResponses responses = new ApiResponses();
        ApiResponse okResponse = new ApiResponse().description("Successful Operation");
        Content responseContent = new Content();
        MediaType responseMediaType = new MediaType();

        String respType = ep.getResponseType() != null ? ep.getResponseType() : "Object";
        responseMediaType.setSchema(mapJavaTypeToOpenApiSchema(respType, null, false, false, isCustomModel(respType)));
        responseContent.addMediaType("application/json", responseMediaType);
        okResponse.setContent(responseContent);

        responses.addApiResponse("200", okResponse);
        operation.setResponses(responses);

        return operation;
    }

    public Schema<?> mapJavaTypeToOpenApiSchema(String javaType, String genericType, boolean isCollection, boolean isMap, boolean isNestedModel) {
        if (isCollection) {
            ArraySchema arraySchema = new ArraySchema();
            String innerType = genericType != null ? genericType : "Object";
            arraySchema.setItems(mapJavaTypeToOpenApiSchema(innerType, null, false, false, isCustomModel(innerType)));
            return arraySchema;
        }

        if (isMap) {
            MapSchema mapSchema = new MapSchema();
            return mapSchema;
        }

        if (isNestedModel || isCustomModel(javaType)) {
            Schema<?> refSchema = new Schema<>();
            refSchema.set$ref("#/components/schemas/" + extractSimpleClassName(javaType));
            return refSchema;
        }

        String simpleType = extractSimpleClassName(javaType);

        switch (simpleType) {
            case "String":
            case "char":
                return new StringSchema();
            case "Integer":
            case "int":
                IntegerSchema int32Schema = new IntegerSchema();
                int32Schema.setFormat("int32");
                return int32Schema;
            case "Long":
            case "long":
                IntegerSchema int64Schema = new IntegerSchema();
                int64Schema.setFormat("int64");
                return int64Schema;
            case "Boolean":
            case "boolean":
                return new BooleanSchema();
            case "Double":
            case "double":
                NumberSchema doubleSchema = new NumberSchema();
                doubleSchema.setFormat("double");
                return doubleSchema;
            case "Float":
            case "float":
                NumberSchema floatSchema = new NumberSchema();
                floatSchema.setFormat("float");
                return floatSchema;
            case "BigDecimal":
                return new NumberSchema();
            case "Date":
            case "LocalDate":
                return new DateSchema();
            case "LocalDateTime":
                return new DateTimeSchema();
            default:
                return new ObjectSchema();
        }
    }

    private boolean isCustomModel(String javaType) {
        if (javaType == null) return false;
        String simple = extractSimpleClassName(javaType);
        return simple.equals("User") || simple.equals("Account") || simple.equals("Transaction") ||
               simple.equals("Loan") || simple.equals("MutualFund") || simple.equals("Investment") ||
               simple.equals("CancelPaymentRequest") || simple.equals("PaymentResponse");
    }

    private String extractSimpleClassName(String typeName) {
        if (typeName == null) return "Object";
        if (typeName.contains("<")) {
            typeName = typeName.substring(0, typeName.indexOf("<"));
        }
        if (typeName.contains(".")) {
            typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        }
        return typeName.trim();
    }
}
