package com.swagger.demo;

import com.swagger.demo.model.ApiEndpoint;
import com.swagger.demo.scanner.JavaSourceControllerScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ControllerScannerCommand {

    private static final Logger logger = LoggerFactory.getLogger(ControllerScannerCommand.class);

    public static void main(String[] args) {
        System.out.println("====================================================================================================");
        System.out.println("                         SPRING MVC & JAX-RS CONTROLLER SOURCE SCANNER                              ");
        System.out.println("====================================================================================================");

        try {
            File currentDir = new File(".").getCanonicalFile();
            File srcDir;
            if (currentDir.getName().equals("swagger-demo")) {
                srcDir = new File(currentDir.getParentFile(), "src/main/java");
            } else {
                srcDir = new File(currentDir, "src/main/java");
            }

            JavaSourceControllerScanner scanner = new JavaSourceControllerScanner();
            List<ApiEndpoint> endpoints = scanner.scanSourceDirectory(srcDir);

            System.out.println();
            System.out.println("Detected APIs (" + endpoints.size() + " endpoints discovered from AST source scan):");
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("%-7s %-35s %-25s %-30s%n", "METHOD", "PATH", "CONTROLLER METHOD", "PARAMETERS");
            System.out.println("----------------------------------------------------------------------------------------------------");

            for (ApiEndpoint ep : endpoints) {
                String controllerMethod = ep.getControllerClass() + "." + ep.getMethodName() + "()";
                String paramsStr = ep.getParameters().isEmpty() ? "None" : ep.getParameters().toString();

                System.out.printf("%-7s %-35s %-25s %-30s%n",
                        ep.getHttpMethod(),
                        ep.getPath(),
                        controllerMethod,
                        paramsStr
                );
            }

            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println("Scan completed successfully! Discovered " + endpoints.size() + " total API endpoints.");
            System.out.println("====================================================================================================");

        } catch (Exception e) {
            logger.error("Error during static AST controller scanning", e);
        }
    }
}
