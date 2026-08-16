package com.swagger.demo.scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.model.ModelField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class JavaSourceModelScanner {

    private static final Logger logger = LoggerFactory.getLogger(JavaSourceModelScanner.class);

    private static final Set<String> COMMON_TYPES = new HashSet<>(Arrays.asList(
            "String", "Integer", "Long", "Boolean", "Double", "Float", "BigDecimal",
            "Date", "LocalDate", "LocalDateTime", "int", "long", "double", "float", "boolean", "byte", "short", "char"
    ));

    private static final Set<String> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
            "int", "long", "double", "float", "boolean", "byte", "short", "char"
    ));

    public List<ApiModelSchema> scanModelDirectory(File modelDir) throws IOException {
        List<ApiModelSchema> schemas = new ArrayList<>();

        if (!modelDir.exists() || !modelDir.isDirectory()) {
            logger.error("Model directory does not exist or is not a directory: {}", modelDir.getAbsolutePath());
            return schemas;
        }

        logger.info("Scanning Java model source files in: {}", modelDir.getAbsolutePath());

        try (Stream<Path> paths = Files.walk(modelDir.toPath())) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            ApiModelSchema schema = parseModelFile(p.toFile());
                            if (schema != null) {
                                schemas.add(schema);
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to parse model file {}: {}", p.getFileName(), e.getMessage());
                        }
                    });
        }

        return schemas;
    }

    public ApiModelSchema parseModelFile(File javaFile) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(javaFile);
        return parseCompilationUnit(cu);
    }

    public ApiModelSchema parseModelSourceCode(String sourceCode) {
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        return parseCompilationUnit(cu);
    }

    private ApiModelSchema parseCompilationUnit(CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .map(p -> p.getNameAsString())
                .orElse("");

        ClassOrInterfaceDeclaration clazz = cu.findFirst(ClassOrInterfaceDeclaration.class).orElse(null);
        if (clazz == null) return null;

        String className = clazz.getNameAsString();
        ApiModelSchema schema = new ApiModelSchema(className, packageName);

        Set<String> methodNames = new HashSet<>();
        for (MethodDeclaration method : clazz.getMethods()) {
            if (method.isPublic()) {
                methodNames.add(method.getNameAsString());
            }
        }

        for (FieldDeclaration field : clazz.getFields()) {
            if (field.isStatic()) continue;

            for (VariableDeclarator var : field.getVariables()) {
                String fieldName = var.getNameAsString();
                Type fieldType = var.getType();

                ModelField modelField = buildModelField(fieldName, fieldType, methodNames);
                schema.getFields().add(modelField);
            }
        }

        return schema;
    }

    private ModelField buildModelField(String fieldName, Type fieldType, Set<String> methodNames) {
        String typeName = fieldType.asString();
        String genericType = null;
        boolean isCollection = false;
        boolean isMap = false;
        boolean isNestedModel = false;
        boolean isNullable = true;

        if (fieldType.isPrimitiveType()) {
            isNullable = false;
        }

        if (fieldType.isClassOrInterfaceType()) {
            ClassOrInterfaceType classType = fieldType.asClassOrInterfaceType();
            String rawName = classType.getNameAsString();

            if ("List".equals(rawName) || "Set".equals(rawName) || "Collection".equals(rawName)) {
                isCollection = true;
                if (classType.getTypeArguments().isPresent() && !classType.getTypeArguments().get().isEmpty()) {
                    genericType = classType.getTypeArguments().get().get(0).asString();
                    if (!COMMON_TYPES.contains(genericType)) {
                        isNestedModel = true;
                    }
                }
            } else if ("Map".equals(rawName)) {
                isMap = true;
                if (classType.getTypeArguments().isPresent() && classType.getTypeArguments().get().size() > 1) {
                    genericType = classType.getTypeArguments().get().get(1).asString();
                    if (!COMMON_TYPES.contains(genericType)) {
                        isNestedModel = true;
                    }
                }
            } else {
                if (!COMMON_TYPES.contains(rawName)) {
                    isNestedModel = true;
                }
            }
        }

        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        boolean hasGetter = methodNames.contains("get" + capitalized) || methodNames.contains("is" + capitalized);
        boolean hasSetter = methodNames.contains("set" + capitalized);

        return new ModelField(
                fieldName, typeName, genericType, isCollection, isMap, isNestedModel, isNullable, hasGetter, hasSetter
        );
    }
}
