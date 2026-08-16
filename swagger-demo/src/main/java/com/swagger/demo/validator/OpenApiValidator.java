package com.swagger.demo.validator;

import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class OpenApiValidator {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiValidator.class);

    public boolean validateSpecFile(File specFile) {
        if (!specFile.exists()) {
            logger.error("Spec file does not exist: {}", specFile.getAbsolutePath());
            return false;
        }

        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        SwaggerParseResult parseResult = parser.readLocation(specFile.getAbsolutePath(), null, null);

        if (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty()) {
            logger.warn("OpenAPI Validation Messages for {}:", specFile.getName());
            for (String message : parseResult.getMessages()) {
                logger.warn(" - {}", message);
            }
        }

        if (parseResult.getOpenAPI() == null) {
            logger.error("OpenAPI specification in {} is syntactically INVALID!", specFile.getName());
            return false;
        }

        logger.info("OpenAPI specification in {} is syntactically VALID!", specFile.getName());
        return true;
    }
}
