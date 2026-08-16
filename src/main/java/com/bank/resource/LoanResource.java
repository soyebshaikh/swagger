package com.bank.resource;

import com.bank.model.Loan;
import com.bank.service.LoanService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private final LoanService loanService;

    public LoanResource(LoanService loanService) {
        this.loanService = loanService;
    }

    @POST
    @Path("/apply")
    public Response applyForLoan(Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String loanType = (String) request.get("loanType");
            BigDecimal principalAmount = new BigDecimal(request.get("principalAmount").toString());
            BigDecimal interestRate = new BigDecimal(request.get("interestRate").toString());
            Integer durationMonths = Integer.valueOf(request.get("durationMonths").toString());

            Loan loan = loanService.applyLoan(userId, loanType, principalAmount, interestRate, durationMonths);
            return Response.status(Response.Status.CREATED).entity(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getLoansByUser(@PathParam("userId") Long userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        return Response.ok(loans).build();
    }
}
