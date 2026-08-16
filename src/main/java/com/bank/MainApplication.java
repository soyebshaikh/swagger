package com.bank;

import com.bank.resource.AccountResource;
import com.bank.resource.AuthResource;
import com.bank.resource.InvestmentResource;
import com.bank.resource.LoanResource;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.Response;
import java.util.Map;

public class MainApplication {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        logger.info("=================================================");
        logger.info("Starting Pure Spring XML + JAX-RS Banking Backend");
        logger.info("=================================================");

        // Load Pure Spring XML Container
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        context.registerShutdownHook();

        logger.info("Spring Application Context loaded successfully (XML Bean Wiring)!");

        // Retrieve JAX-RS Resource Beans
        AuthResource authResource = context.getBean("authResource", AuthResource.class);
        AccountResource accountResource = context.getBean("accountResource", AccountResource.class);
        LoanResource loanResource = context.getBean("loanResource", LoanResource.class);
        InvestmentResource investmentResource = context.getBean("investmentResource", InvestmentResource.class);
        ProducerTemplate producerTemplate = context.getBean("template", ProducerTemplate.class);
        CamelContext camelContext = context.getBean("bankingCamelContext", CamelContext.class);

        logger.info("Camel Context Status: {} (Active Routes: {})", camelContext.getStatus(), camelContext.getRoutes().size());

        try {
            // 1. JAX-RS AuthResource (@Path /auth/login)
            logger.info("--- [1. JAX-RS AUTH RESOURCE: POST /auth/login] ---");
            Response authResp = authResource.login(Map.of("username", "john_doe", "password", "password123"));
            logger.info("Login HTTP Status: {}, Response Entity: {}", authResp.getStatus(), authResp.getEntity());

            // 2. JAX-RS AccountResource (@GET /accounts/balance)
            logger.info("--- [2. JAX-RS ACCOUNT RESOURCE: GET /accounts/balance] ---");
            Response balResp = accountResource.getBalance("ACC-CHK-1001");
            logger.info("Balance HTTP Status: {}, Entity: {}", balResp.getStatus(), balResp.getEntity());

            // 3. JAX-RS LoanResource (@POST /loans/apply)
            logger.info("--- [3. JAX-RS LOAN RESOURCE: POST /loans/apply] ---");
            Response loanResp = loanResource.applyForLoan(Map.of(
                    "userId", "1",
                    "loanType", "PERSONAL",
                    "principalAmount", "15000.00",
                    "interestRate", "9.50",
                    "durationMonths", "24"
            ));
            logger.info("Loan HTTP Status: {}, Entity: {}", loanResp.getStatus(), loanResp.getEntity());

            // 4. JAX-RS InvestmentResource (@GET /investments/funds)
            logger.info("--- [4. JAX-RS INVESTMENT RESOURCE: GET /investments/funds] ---");
            Response fundsResp = investmentResource.getAllMutualFunds();
            logger.info("Funds HTTP Status: {}, Entity: {}", fundsResp.getStatus(), fundsResp.getEntity());

            logger.info("=================================================");
            logger.info("Pure Spring XML + JAX-RS Backend Running Cleanly!");
            logger.info("=================================================");

            // Shutdown ProducerTemplate and Context
            if (producerTemplate != null) {
                producerTemplate.stop();
            }
            context.close();

        } catch (Exception e) {
            logger.error("Error during application execution", e);
            context.close();
        }
    }
}
