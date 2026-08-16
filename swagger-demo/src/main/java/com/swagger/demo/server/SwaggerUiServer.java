package com.swagger.demo.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class SwaggerUiServer {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerUiServer.class);

    private int port;
    private final File specJsonFile;
    private final File specYamlFile;
    private HttpServer server;

    public SwaggerUiServer(int initialPort, File specJsonFile, File specYamlFile) {
        this.port = initialPort;
        this.specJsonFile = specJsonFile;
        this.specYamlFile = specYamlFile;
    }

    public void start() throws IOException {
        int attempts = 0;
        while (attempts < 10) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                break;
            } catch (BindException e) {
                logger.warn("Port {} in use, trying port {}", port, port + 1);
                port++;
                attempts++;
            }
        }

        if (server == null) {
            server = HttpServer.create(new InetSocketAddress(0), 0);
            this.port = server.getAddress().getPort();
        }

        // Serve openapi.yaml
        server.createContext("/openapi.yaml", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (!specYamlFile.exists()) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] responseBytes = Files.readAllBytes(specYamlFile.toPath());
                exchange.getResponseHeaders().set("Content-Type", "text/yaml; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        });

        // Serve openapi.json
        server.createContext("/openapi.json", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (!specJsonFile.exists()) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] responseBytes = Files.readAllBytes(specJsonFile.toPath());
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        });

        // Serve Swagger UI at /swagger-ui/
        HttpHandler swaggerUiHandler = new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String html = getSwaggerUiHtml();
                byte[] responseBytes = html.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        };

        server.createContext("/swagger-ui/", swaggerUiHandler);
        server.createContext("/swagger-ui", swaggerUiHandler);

        server.setExecutor(null);
        server.start();

        logger.info("Swagger UI HTTP Server started at: http://localhost:{}/swagger-ui/", port);
        logger.info("OpenAPI YAML Specification available at: http://localhost:{}/openapi.yaml", port);
    }

    public int getPort() {
        return port;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            logger.info("Swagger UI HTTP Server stopped.");
        }
    }

    private String getSwaggerUiHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Apex Banking API - Swagger UI Demo</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui.css\">\n" +
                "    <style>\n" +
                "        html { box-sizing: border-box; overflow: -moz-scrollbars-vertical; overflow-y: scroll; }\n" +
                "        *, *:before, *:after { box-sizing: inherit; }\n" +
                "        body { margin: 0; background: #fafafa; font-family: sans-serif; }\n" +
                "        .topbar { background-color: #1b1e24; padding: 12px 24px; color: #ffffff; font-size: 20px; font-weight: bold; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }\n" +
                "        .topbar-tag { background: #007bff; color: #fff; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: normal; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"topbar\">\n" +
                "        <span>🏦 Apex Banking Backend REST API</span>\n" +
                "        <span class=\"topbar-tag\">OpenAPI 3.1 Demo (openapi.yaml)</span>\n" +
                "    </div>\n" +
                "    <div id=\"swagger-ui\"></div>\n" +
                "    <script src=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js\" charset=\"UTF-8\"></script>\n" +
                "    <script src=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui-standalone-preset.js\" charset=\"UTF-8\"></script>\n" +
                "    <script>\n" +
                "        window.onload = function() {\n" +
                "            const ui = SwaggerUIBundle({\n" +
                "                url: \"/openapi.yaml\",\n" +
                "                dom_id: '#swagger-ui',\n" +
                "                deepLinking: true,\n" +
                "                presets: [\n" +
                "                    SwaggerUIBundle.presets.apis,\n" +
                "                    SwaggerUIStandalonePreset\n" +
                "                ],\n" +
                "                plugins: [\n" +
                "                    SwaggerUIBundle.plugins.DownloadUrl\n" +
                "                ],\n" +
                "                layout: \"StandaloneLayout\"\n" +
                "            });\n" +
                "            window.ui = ui;\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
