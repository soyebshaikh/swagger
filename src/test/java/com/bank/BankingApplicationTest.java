package com.bank;

import com.bank.model.Account;
import com.bank.model.Investment;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.resource.AccountResource;
import com.bank.resource.AuthResource;
import com.bank.resource.InvestmentResource;
import com.bank.resource.LoanResource;
import com.bank.service.AccountService;
import com.bank.service.AuthService;
import com.bank.service.InvestmentService;
import com.bank.service.LoanService;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class BankingApplicationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private AuthResource authResource;

    @Autowired
    private AccountResource accountResource;

    @Autowired
    private LoanResource loanResource;

    @Autowired
    private InvestmentResource investmentResource;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    @DisplayName("Test 1: User Login via JAX-RS AuthResource (@Path /auth/login)")
    public void testAuthResourceLogin() {
        Map<String, String> credentials = Map.of("username", "john_doe", "password", "password123");
        Response response = authResource.login(credentials);

        assertEquals(200, response.getStatus());
        User user = (User) response.getEntity();
        assertNotNull(user);
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    @DisplayName("Test 2: Account Balance Query via JAX-RS AccountResource (@GET /accounts/balance)")
    public void testAccountResourceBalance() {
        Response response = accountResource.getBalance("ACC-CHK-1001");
        assertEquals(200, response.getStatus());

        Map<?, ?> result = (Map<?, ?>) response.getEntity();
        assertEquals("ACC-CHK-1001", result.get("accountNumber"));
        assertNotNull(result.get("balance"));
    }

    @Test
    @DisplayName("Test 3: Withdrawal via JAX-RS AccountResource (@POST /accounts/withdraw)")
    public void testAccountResourceWithdrawal() {
        Map<String, Object> request = Map.of(
                "accountNumber", "ACC-CHK-1001",
                "amount", "500.00",
                "description", "JAX-RS Test Withdrawal"
        );
        Response response = accountResource.withdraw(request);
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Test 4: Loan Application via JAX-RS LoanResource (@POST /loans/apply)")
    public void testLoanResourceApply() {
        Map<String, Object> request = Map.of(
                "userId", "1",
                "loanType", "PERSONAL",
                "principalAmount", "12000.00",
                "interestRate", "8.50",
                "durationMonths", "12"
        );
        Response response = loanResource.applyForLoan(request);
        assertEquals(201, response.getStatus());

        Loan loan = (Loan) response.getEntity();
        assertNotNull(loan);
        assertEquals("APPROVED", loan.getStatus());
    }

    @Test
    @DisplayName("Test 5: Mutual Fund Purchase via JAX-RS InvestmentResource (@POST /investments/buy)")
    public void testInvestmentResourceBuy() {
        Map<String, Object> request = Map.of(
                "userId", "2",
                "fundCode", "MF-EQ-001",
                "amount", "1425.00",
                "investmentType", "SIP"
        );
        Response response = investmentResource.buyMutualFund(request);
        assertEquals(201, response.getStatus());

        Investment investment = (Investment) response.getEntity();
        assertNotNull(investment);
        assertEquals(new BigDecimal("10.0000"), investment.getUnits());
    }

    @Test
    @DisplayName("Test 6: Apache Camel Direct Route Execution")
    public void testCamelRoutes() {
        Map<String, Object> req = new HashMap<>();
        req.put("accountNumber", "ACC-SAV-1002");
        req.put("amount", new BigDecimal("1000.00"));
        req.put("description", "Camel Route Test");

        Object response = producerTemplate.requestBody("direct:processWithdrawal", req);
        assertNotNull(response);
    }
}
