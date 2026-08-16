package com.swagger.demo.generator;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OpenApiSpecGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiSpecGenerator.class);

    private final File outputDirectory;

    public OpenApiSpecGenerator(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
    }

    public File generateJsonSpec(OpenAPI openAPI) throws IOException {
        File jsonFile = new File(outputDirectory, "openapi.json");
        ObjectMapper mapper = Json.mapper();
        String jsonContent = mapper.writer(new DefaultPrettyPrinter()).writeValueAsString(openAPI);

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonContent);
        }

        logger.info("Successfully generated OpenAPI JSON spec at: {}", jsonFile.getAbsolutePath());
        return jsonFile;
    }

    public File generateYamlSpec(OpenAPI openAPI) throws IOException {
        File yamlFile = new File(outputDirectory, "openapi.yaml");
        ObjectMapper mapper = Yaml.mapper();
        String yamlContent = mapper.writeValueAsString(openAPI);

        try (FileWriter writer = new FileWriter(yamlFile)) {
            writer.write(yamlContent);
        }

        logger.info("Successfully generated OpenAPI YAML spec at: {}", yamlFile.getAbsolutePath());
        return yamlFile;
    }
}
