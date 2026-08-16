package com.swagger.demo.scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.swagger.demo.model.ApiEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JavaSourceControllerScanner {

    private static final Logger logger = LoggerFactory.getLogger(JavaSourceControllerScanner.class);

    public List<ApiEndpoint> scanSourceDirectory(File sourceDir) throws IOException {
        List<ApiEndpoint> endpoints = new ArrayList<>();

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            logger.error("Source directory does not exist or is not a directory: {}", sourceDir.getAbsolutePath());
            return endpoints;
        }

        logger.info("Scanning Java source files recursively in: {}", sourceDir.getAbsolutePath());

        try (Stream<Path> paths = Files.walk(sourceDir.toPath())) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> parseJavaFile(p.toFile(), endpoints));
        }

        return endpoints;
    }

    private void parseJavaFile(File javaFile, List<ApiEndpoint> endpoints) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            String packageName = cu.getPackageDeclaration()
                    .map(p -> p.getNameAsString())
                    .orElse("");

            for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                boolean isSpringController = clazz.isAnnotationPresent("Controller") || clazz.isAnnotationPresent("RestController");
                boolean isJaxRsResource = clazz.isAnnotationPresent("Path");
                boolean isResourcePackage = packageName.contains("resource") || packageName.contains("controller");

                if (!isSpringController && !isJaxRsResource && !isResourcePackage) {
                    continue; // Skip non-controller classes
                }

                String className = clazz.getNameAsString();
                String classBasePath = extractClassBasePath(clazz);

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!method.isPublic()) continue;

                    String httpMethod = extractHttpMethod(method);
                    if (httpMethod == null) continue; // Skip non-endpoint methods

                    String methodPath = extractMethodPath(method);
                    String combinedPath = normalizePath(classBasePath + methodPath);

                    ApiEndpoint endpoint = new ApiEndpoint();
                    endpoint.setControllerClass(className);
                    endpoint.setControllerPackage(packageName);
                    endpoint.setMethodName(method.getNameAsString());
                    endpoint.setHttpMethod(httpMethod);
                    endpoint.setPath(combinedPath);
                    endpoint.setResponseType(method.getTypeAsString());

                    // Parameter Inspection
                    for (Parameter p : method.getParameters()) {
                        ApiEndpoint.ApiParameter apiParam = extractParameterInfo(p);
                        endpoint.getParameters().add(apiParam);
                        if ("RequestBody".equals(apiParam.getAnnotationType()) || apiParam.getAnnotationType() == null) {
                            endpoint.setRequestType(p.getTypeAsString());
                        }
                    }

                    endpoints.add(endpoint);
                    logger.info("AST Discovered Endpoint: {} {} -> {}.{}()",
                            httpMethod, combinedPath, className, method.getNameAsString());
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse Java source file {}: {}", javaFile.getName(), e.getMessage());
        }
    }

    private String extractClassBasePath(ClassOrInterfaceDeclaration clazz) {
        if (clazz.isAnnotationPresent("RequestMapping")) {
            return extractAnnotationValue(clazz.getAnnotationByName("RequestMapping").get());
        }
        if (clazz.isAnnotationPresent("Path")) {
            return extractAnnotationValue(clazz.getAnnotationByName("Path").get());
        }
        return "";
    }

    private String extractHttpMethod(MethodDeclaration method) {
        if (method.isAnnotationPresent("GetMapping") || method.isAnnotationPresent("GET")) return "GET";
        if (method.isAnnotationPresent("PostMapping") || method.isAnnotationPresent("POST")) return "POST";
        if (method.isAnnotationPresent("PutMapping") || method.isAnnotationPresent("PUT")) return "PUT";
        if (method.isAnnotationPresent("DeleteMapping") || method.isAnnotationPresent("DELETE")) return "DELETE";
        if (method.isAnnotationPresent("PatchMapping")) return "PATCH";
        if (method.isAnnotationPresent("RequestMapping")) {
            AnnotationExpr ann = method.getAnnotationByName("RequestMapping").get();
            return extractHttpMethodFromRequestMapping(ann);
        }
        return null;
    }

    private String extractHttpMethodFromRequestMapping(AnnotationExpr ann) {
        if (ann instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr norm = (NormalAnnotationExpr) ann;
            for (MemberValuePair pair : norm.getPairs()) {
                if ("method".equals(pair.getNameAsString())) {
                    String val = pair.getValue().toString();
                    if (val.contains("GET")) return "GET";
                    if (val.contains("POST")) return "POST";
                    if (val.contains("PUT")) return "PUT";
                    if (val.contains("DELETE")) return "DELETE";
                }
            }
        }
        return "GET"; // Default for @RequestMapping
    }

    private String extractMethodPath(MethodDeclaration method) {
        String[] mappings = {"GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping", "RequestMapping", "Path"};
        for (String annName : mappings) {
            if (method.isAnnotationPresent(annName)) {
                return extractAnnotationValue(method.getAnnotationByName(annName).get());
            }
        }
        return "";
    }

    private String extractAnnotationValue(AnnotationExpr ann) {
        if (ann instanceof SingleMemberAnnotationExpr) {
            SingleMemberAnnotationExpr single = (SingleMemberAnnotationExpr) ann;
            return cleanPathValue(single.getMemberValue().toString());
        } else if (ann instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr norm = (NormalAnnotationExpr) ann;
            for (MemberValuePair pair : norm.getPairs()) {
                if ("value".equals(pair.getNameAsString()) || "path".equals(pair.getNameAsString())) {
                    return cleanPathValue(pair.getValue().toString());
                }
            }
        }
        return "";
    }

    private String cleanPathValue(String val) {
        val = val.replace("\"", "").replace("{", "").replace("}", "").trim();
        if (val.startsWith("[")) val = val.substring(1);
        if (val.endsWith("]")) val = val.substring(0, val.length() - 1);
        return val.trim();
    }

    private ApiEndpoint.ApiParameter extractParameterInfo(Parameter p) {
        String paramName = p.getNameAsString();
        String paramType = p.getTypeAsString();
        String annType = null;
        boolean required = false;

        if (p.isAnnotationPresent("PathVariable")) {
            annType = "PathVariable";
            required = true;
            paramName = getAnnotationValOrDefault(p.getAnnotationByName("PathVariable"), paramName);
        } else if (p.isAnnotationPresent("PathParam")) {
            annType = "PathParam";
            required = true;
            paramName = getAnnotationValOrDefault(p.getAnnotationByName("PathParam"), paramName);
        } else if (p.isAnnotationPresent("RequestParam")) {
            annType = "RequestParam";
            paramName = getAnnotationValOrDefault(p.getAnnotationByName("RequestParam"), paramName);
        } else if (p.isAnnotationPresent("QueryParam")) {
            annType = "QueryParam";
            paramName = getAnnotationValOrDefault(p.getAnnotationByName("QueryParam"), paramName);
        } else if (p.isAnnotationPresent("RequestBody")) {
            annType = "RequestBody";
            required = true;
        }

        return new ApiEndpoint.ApiParameter(paramName, paramType, annType, required);
    }

    private String getAnnotationValOrDefault(Optional<AnnotationExpr> annOpt, String defaultVal) {
        if (annOpt.isPresent()) {
            String val = extractAnnotationValue(annOpt.get());
            if (!val.isEmpty()) return val;
        }
        return defaultVal;
    }

    private String normalizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path.replaceAll("//+", "/");
    }
}
