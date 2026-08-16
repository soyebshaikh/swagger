package com.swagger.demo.scanner;

import com.bank.resource.AccountResource;
import com.bank.resource.AuthResource;
import com.bank.resource.InvestmentResource;
import com.bank.resource.LoanResource;
import com.bank.resource.PaymentResource;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JaxRsApiScanner {

    private static final Logger logger = LoggerFactory.getLogger(JaxRsApiScanner.class);

    private static final List<Class<?>> RESOURCE_CLASSES = Arrays.asList(
            AuthResource.class,
            AccountResource.class,
            LoanResource.class,
            InvestmentResource.class,
            PaymentResource.class
    );

    public OpenAPI scanAndBuildOpenApi() {
        logger.info("Scanning JAX-RS Resource classes for REST endpoints...");

        OpenAPI openAPI = new OpenAPI();

        // Info Metadata
        Info info = new Info()
                .title("Apex Banking Application REST API")
                .version("1.0.0")
                .description("Auto-generated OpenAPI 3.0 specification discovered via JAX-RS annotation scanner (Zero code modifications).");
        openAPI.setInfo(info);

        // Server Metadata
        Server server = new Server()
                .url("http://localhost:8088")
                .description("Standalone Banking Application Server");
        openAPI.setServers(List.of(server));

        Paths paths = new Paths();

        for (Class<?> resourceClass : RESOURCE_CLASSES) {
            String basePath = "";
            if (resourceClass.isAnnotationPresent(Path.class)) {
                basePath = resourceClass.getAnnotation(Path.class).value();
            }

            String defaultProduces = "application/json";
            if (resourceClass.isAnnotationPresent(Produces.class)) {
                String[] p = resourceClass.getAnnotation(Produces.class).value();
                if (p.length > 0) defaultProduces = p[0];
            }

            String defaultConsumes = "application/json";
            if (resourceClass.isAnnotationPresent(Consumes.class)) {
                String[] c = resourceClass.getAnnotation(Consumes.class).value();
                if (c.length > 0) defaultConsumes = c[0];
            }

            for (Method method : resourceClass.getDeclaredMethods()) {
                String httpMethod = null;
                if (method.isAnnotationPresent(GET.class)) httpMethod = "GET";
                else if (method.isAnnotationPresent(POST.class)) httpMethod = "POST";
                else if (method.isAnnotationPresent(PUT.class)) httpMethod = "PUT";
                else if (method.isAnnotationPresent(DELETE.class)) httpMethod = "DELETE";

                if (httpMethod == null) continue; // Skip non-HTTP methods

                String subPath = "";
                if (method.isAnnotationPresent(Path.class)) {
                    subPath = method.getAnnotation(Path.class).value();
                }

                String fullPath = normalizePath(basePath + subPath);
                logger.info("Discovered Endpoint: [{}] {}", httpMethod, fullPath);

                PathItem pathItem = paths.get(fullPath);
                if (pathItem == null) {
                    pathItem = new PathItem();
                    paths.addPathItem(fullPath, pathItem);
                }

                Operation operation = new Operation();
                operation.setSummary("Endpoint: " + method.getName());
                operation.setDescription("Discovered automatically from method " + resourceClass.getSimpleName() + "." + method.getName() + "()");

                // Parameters
                List<Parameter> parameters = new ArrayList<>();
                java.lang.reflect.Parameter[] methodParams = method.getParameters();

                boolean hasRequestBody = false;

                for (java.lang.reflect.Parameter p : methodParams) {
                    if (p.isAnnotationPresent(QueryParam.class)) {
                        QueryParam qp = p.getAnnotation(QueryParam.class);
                        QueryParameter param = new QueryParameter();
                        param.setName(qp.value());
                        param.setRequired(false);
                        param.setSchema(new StringSchema());
                        parameters.add(param);
                    } else if (p.isAnnotationPresent(PathParam.class)) {
                        PathParam pp = p.getAnnotation(PathParam.class);
                        PathParameter param = new PathParameter();
                        param.setName(pp.value());
                        param.setRequired(true);
                        param.setSchema(new StringSchema());
                        parameters.add(param);
                    } else {
                        hasRequestBody = true;
                    }
                }

                if (!parameters.isEmpty()) {
                    operation.setParameters(parameters);
                }

                // Request Body for POST/PUT
                if (hasRequestBody && ("POST".equalsIgnoreCase(httpMethod) || "PUT".equalsIgnoreCase(httpMethod))) {
                    RequestBody requestBody = new RequestBody();
                    requestBody.setDescription("JSON payload for " + method.getName());
                    requestBody.setRequired(true);

                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    mediaType.setSchema(new ObjectSchema().description("Payload fields map or DTO"));
                    content.addMediaType(defaultConsumes, mediaType);
                    requestBody.setContent(content);

                    operation.setRequestBody(requestBody);
                }

                // Api Responses
                ApiResponses responses = new ApiResponses();
                ApiResponse okResponse = new ApiResponse().description("Successful Operation");
                Content responseContent = new Content();
                MediaType responseMediaType = new MediaType();
                responseMediaType.setSchema(new ObjectSchema().description("JSON response payload"));
                responseContent.addMediaType(defaultProduces, responseMediaType);
                okResponse.setContent(responseContent);
                responses.addApiResponse("200", okResponse);

                operation.setResponses(responses);

                // Assign operation to PathItem
                if ("GET".equalsIgnoreCase(httpMethod)) pathItem.setGet(operation);
                else if ("POST".equalsIgnoreCase(httpMethod)) pathItem.setPost(operation);
                else if ("PUT".equalsIgnoreCase(httpMethod)) pathItem.setPut(operation);
                else if ("DELETE".equalsIgnoreCase(httpMethod)) pathItem.setDelete(operation);
            }
        }

        openAPI.setPaths(paths);
        return openAPI;
    }

    private String normalizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path.replaceAll("//+", "/");
    }
}
