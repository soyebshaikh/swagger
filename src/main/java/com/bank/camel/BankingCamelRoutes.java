package com.bank.camel;

import org.apache.camel.builder.RouteBuilder;

public class BankingCamelRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Camel Route: Direct Login Endpoint
        from("direct:userLogin")
                .routeId("userLoginRoute")
                .log("[CAMEL LOGIN ROUTE] Login attempt for user: ${body[username]}")
                .bean("authService", "login(${body[username]}, ${body[password]})")
                .log("[CAMEL LOGIN ROUTE] Login successful: ${body}");

        // Camel Route: Account Balance Inquirer
        from("direct:fetchAccountBalance")
                .routeId("fetchAccountBalanceRoute")
                .log("[CAMEL BALANCE ROUTE] Querying balance for account: ${body}")
                .bean("accountService", "getAccountByNumber(${body})")
                .log("[CAMEL BALANCE ROUTE] Account found: ${body}");

        // Camel Route: Withdrawal Processing
        from("direct:processWithdrawal")
                .routeId("processWithdrawalRoute")
                .log("[CAMEL WITHDRAWAL ROUTE] Processing withdrawal for account: ${body[accountNumber]}, amount: ${body[amount]}")
                .bean("accountService", "withdraw(${body[accountNumber]}, ${body[amount]}, ${body[description]})")
                .process(new BankingProcessors.TransactionEventTransformer());

        // Camel Route: Apply Loan Route
        from("direct:applyForLoan")
                .routeId("applyForLoanRoute")
                .log("[CAMEL LOAN ROUTE] Processing loan application for user: ${body[userId]}, principal: ${body[principal]}")
                .bean("loanService", "applyLoan(${body[userId]}, ${body[loanType]}, ${body[principal]}, ${body[interestRate]}, ${body[duration]})");

        // Camel Route: Buy Mutual Fund
        from("direct:purchaseMutualFund")
                .routeId("purchaseMutualFundRoute")
                .log("[CAMEL INVESTMENT ROUTE] Buying fund ${body[fundCode]} for user: ${body[userId]}, amount: ${body[amount]}")
                .bean("investmentService", "buyMutualFund(${body[userId]}, ${body[fundCode]}, ${body[amount]}, ${body[investmentType]})");
    }
}
