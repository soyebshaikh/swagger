package com.bank.resource;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.AccountService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Path("/balance")
    public Response getBalance(@QueryParam("accountNumber") String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "accountNumber parameter is required"))
                    .build();
        }
        try {
            BigDecimal balance = accountService.getBalance(accountNumber);
            return Response.ok(Map.of("accountNumber", accountNumber, "balance", balance)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/details")
    public Response getAccountDetails(@QueryParam("accountNumber") String accountNumber) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/withdraw")
    public Response withdraw(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");

        try {
            Transaction txn = accountService.withdraw(accountNumber, amount, description);
            return Response.ok(txn).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/deposit")
    public Response deposit(Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");

        try {
            Transaction txn = accountService.deposit(accountNumber, amount, description);
            return Response.ok(txn).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/transactions")
    public Response getTransactions(@QueryParam("accountNumber") String accountNumber) {
        List<Transaction> txns = accountService.getTransactions(accountNumber);
        return Response.ok(txns).build();
    }
}
