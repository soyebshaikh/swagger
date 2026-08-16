package com.bank.resource;

import com.bank.model.Investment;
import com.bank.model.MutualFund;
import com.bank.service.InvestmentService;

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

@Path("/investments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestmentResource {

    private final InvestmentService investmentService;

    public InvestmentResource(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GET
    @Path("/funds")
    public Response getAllMutualFunds() {
        List<MutualFund> funds = investmentService.getAllMutualFunds();
        return Response.ok(funds).build();
    }

    @POST
    @Path("/buy")
    public Response buyMutualFund(Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String fundCode = (String) request.get("fundCode");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String investmentType = (String) request.get("investmentType");

            Investment investment = investmentService.buyMutualFund(userId, fundCode, amount, investmentType);
            return Response.status(Response.Status.CREATED).entity(investment).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserInvestments(@PathParam("userId") Long userId) {
        List<Investment> portfolio = investmentService.getUserInvestments(userId);
        return Response.ok(portfolio).build();
    }
}
