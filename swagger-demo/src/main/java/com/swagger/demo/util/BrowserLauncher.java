package com.swagger.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class BrowserLauncher {

    private static final Logger logger = LoggerFactory.getLogger(BrowserLauncher.class);

    public static boolean verifyServerResponding(String healthCheckUrl, int timeoutMs) {
        logger.info("Verifying Swagger UI server availability at: {}", healthCheckUrl);
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                URL url = new URL(healthCheckUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(500);
                conn.setReadTimeout(500);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    logger.info("Server verification successful! Received HTTP 200 OK.");
                    return true;
                }
            } catch (Exception e) {
                // Retry until timeout
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.warn("Server health verification timed out after {} ms", timeoutMs);
        return false;
    }

    public static void openBrowser(String url) {
        System.out.println("Swagger UI started.");
        System.out.println();
        System.out.println("Opening:");
        System.out.println(url);
        System.out.println();

        boolean launched = false;

        // Strategy 1: Java Desktop Standard API
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    logger.info("Browser launched successfully via standard Java Desktop API.");
                    launched = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Standard Java Desktop API launch failed: {}", e.getMessage());
        }

        if (launched) return;

        // Strategy 2: Operating System Specific Command Line Launchers
        String osName = System.getProperty("os.name", "").toLowerCase();

        try {
            if (osName.contains("win")) {
                // Windows Fallback
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                logger.info("Browser launched via Windows rundll32 fallback.");
                launched = true;
            } else if (osName.contains("mac")) {
                // macOS Fallback
                Runtime.getRuntime().exec(new String[]{"open", url});
                logger.info("Browser launched via macOS 'open' fallback.");
                launched = true;
            } else if (osName.contains("nix") || osName.contains("nux")) {
                // Linux Fallback
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                logger.info("Browser launched via Linux 'xdg-open' fallback.");
                launched = true;
            }
        } catch (Exception e) {
            logger.warn("OS fallback browser launch failed: {}", e.getMessage());
        }

        if (!launched) {
            logger.warn("Automatic browser launching is unavailable in this environment. Please open the URL manually.");
        }
    }
}
